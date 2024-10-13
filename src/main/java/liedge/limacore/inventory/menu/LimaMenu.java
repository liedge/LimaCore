package liedge.limacore.inventory.menu;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import liedge.limacore.LimaCore;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.network.packet.ClientboundMenuDataWatcherPacket;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.limacore.util.LimaCoreUtil;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class LimaMenu<CTX> extends AbstractContainerMenu implements DataWatcherHolder
{
    public static final int DEFAULT_INV_X = 8;
    public static final int DEFAULT_INV_Y = 84;
    public static final int DEFAULT_HOTBAR_Y = 142;

    // Commonly used menu properties
    private final LimaMenuType<CTX, ?> type;
    private final List<LimaDataWatcher<?>> dataWatchers;
    private final Int2ObjectMap<EventHandler<?>> buttonEventHandlers;
    protected final Inventory playerInventory;
    protected final CTX menuContext;
    private final Level level;
    private final ServerPlayer user;
    private boolean firstTick = true;

    protected int inventoryStart;
    protected int inventoryEnd;
    protected int hotbarStart;
    protected int hotbarEnd;

    protected LimaMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId);

        this.type = type;
        this.menuContext = menuContext;
        this.playerInventory = inventory;
        this.level = playerInventory.player.level();
        this.user = LimaCoreUtil.castOrNull(ServerPlayer.class, playerInventory.player);
        this.dataWatchers = createDataWatchers();

        EventHandlerBuilder handlerBuilder = new EventHandlerBuilder();
        defineButtonEventHandlers(handlerBuilder);
        this.buttonEventHandlers = handlerBuilder.map != null ? Int2ObjectMaps.unmodifiable(handlerBuilder.map) : Int2ObjectMaps.emptyMap();
    }

    @Override
    public final List<LimaDataWatcher<?>> getDataWatchers()
    {
        return dataWatchers;
    }

    @Override
    public <T> void sendDataWatcherPacket(int index, NetworkSerializer<T> serializer, T data)
    {
        getUser().connection.send(new ClientboundMenuDataWatcherPacket<>(this.containerId, index, serializer, data));
    }

    @Override
    public boolean stillValid(Player player)
    {
        return type.canPlayerKeepUsing(menuContext, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot.hasItem())
        {
            ItemStack stack1 = slot.getItem();
            stack = stack1.copy();

            if (!quickMoveInternal(index, stack1)) return ItemStack.EMPTY;

            if (stack1.isEmpty())
            {
                slot.setByPlayer(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
                if (slot instanceof LimaItemHandlerMenuSlot limaSlot) limaSlot.setBaseContainerChanged();
            }
        }

        return stack;
    }

    @Override
    public final void broadcastChanges()
    {
        super.broadcastChanges();

        if (firstTick)
        {
            forceSyncDataWatchers();
            firstTick = false;
        }

        tickDataWatchers();
    }

    protected void defineButtonEventHandlers(EventHandlerBuilder builder) {}

    public CTX menuContext()
    {
        return menuContext;
    }

    public Level level()
    {
        return level;
    }

    public ServerPlayer getUser()
    {
        return Objects.requireNonNull(user, "Attempted to access server menu user on client.");
    }

    @SuppressWarnings("unchecked")
    @ApiStatus.Internal
    public final <T> void handleCustomButtonData(ServerPlayer sender, int buttonId, NetworkSerializer<T> serializer, T data)
    {
        if (buttonEventHandlers.containsKey(buttonId))
        {
            EventHandler<?> rawHandler = buttonEventHandlers.get(buttonId);
            if (rawHandler.serializer == serializer)
            {
                EventHandler<T> handler = (EventHandler<T>) rawHandler;
                handler.action().accept(sender, data);
            }
            else
            {
                LimaCore.LOGGER.warn("Received custom button data with mismatching data types: expected {} but received {}", rawHandler.serializer().id(), serializer.id());
            }
        }
        else
        {
            LimaCore.LOGGER.warn("Received custom button data with invalid ID {}", buttonId);
        }
    }

    //#region Quick move functions
    protected abstract boolean quickMoveInternal(int index, ItemStack stack);

    protected boolean quickMoveToContainerSlot(ItemStack stack, int slot)
    {
        return quickMoveToContainerSlots(stack, slot, slot + 1, false);
    }

    protected boolean quickMoveToContainerSlots(ItemStack stack, int startInclusive, int endExclusive, boolean reverse)
    {
        return moveItemStackTo(stack, startInclusive, endExclusive, reverse);
    }

    protected boolean quickMoveToInventory(ItemStack stack, boolean reverse)
    {
        return moveItemStackTo(stack, inventoryStart, inventoryEnd, reverse);
    }

    protected boolean quickMoveToHotbar(ItemStack stack, boolean reverse)
    {
        return moveItemStackTo(stack, hotbarStart, hotbarEnd, reverse);
    }

    protected boolean quickMoveToAllInventory(ItemStack stack, boolean reverse)
    {
        if (quickMoveToInventory(stack, reverse))
        {
            return true;
        }
        else
        {
            return quickMoveToHotbar(stack, reverse);
        }
    }
    //#endregion

    protected <T> void addSlotsGrid(T container, int startIndex, int xPos, int yPos, int columns, int rows, MenuSlotFactory<? super T> factory)
    {
        for (int y = 0; y < rows; y++)
        {
            for (int x = 0; x < columns; x++)
            {
                addSlot(factory.createSlot(container, startIndex + (columns * y + x), xPos + x * 18, yPos + y * 18));
            }
        }
    }

    protected void addRecipeResultSlot(IItemHandlerModifiable itemHandler, int slotIndex, int xPos, int yPos, RecipeType<?> recipeType)
    {
        addSlot(new RecipeResultMenuSlot(itemHandler, slotIndex, xPos, yPos, playerInventory.player, recipeType));
    }

    protected void addRecipeResultSlot(IItemHandlerModifiable itemHandler, int slotIndex, int xPos, int yPos, Holder<RecipeType<?>> holder)
    {
        addRecipeResultSlot(itemHandler, slotIndex, xPos, yPos, holder.value());
    }

    protected void addPlayerInventory(int xPos, int yPos, MenuSlotFactory<Container> factory)
    {
        inventoryStart = slots.size();

        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                addSlot(factory.createSlot(playerInventory, 9 + y * 9 + x, xPos + x * 18, yPos + y * 18));
            }
        }

        inventoryEnd = slots.size();
    }

    protected void addPlayerInventory(int xPos, int yPos)
    {
        addPlayerInventory(xPos, yPos, Slot::new);
    }

    protected void addPlayerHotbar(int xPos, int yPos, MenuSlotFactory<Container> factory)
    {
        hotbarStart = slots.size();

        for (int x = 0; x < 9; x++)
        {
            addSlot(factory.createSlot(playerInventory, x, xPos + x * 18, yPos));
        }

        hotbarEnd = slots.size();
    }

    protected void addPlayerHotbar(int xPos, int yPos)
    {
        addPlayerHotbar(xPos, yPos, Slot::new);
    }

    protected Slot lockedSlot(Container container, int index, int x, int y)
    {
        return new Slot(container, index, x, y)
        {
            @Override
            public boolean mayPlace(ItemStack stack)
            {
                return false;
            }

            @Override
            public boolean mayPickup(Player player)
            {
                return false;
            }

            @Override
            public ItemStack remove(int amount)
            {
                return ItemStack.EMPTY;
            }
        };
    }

    @FunctionalInterface
    public interface MenuSlotFactory<T>
    {
        Slot createSlot(T container, int index, int x, int y);
    }

    protected static class EventHandlerBuilder
    {
        private Int2ObjectMap<EventHandler<?>> map;

        public <T> void handleAction(int index, NetworkSerializer<T> serializer, BiConsumer<ServerPlayer, T> action)
        {
            if (map == null) map = new Int2ObjectOpenHashMap<>(); // Only initialized if used

            LimaCollectionsUtil.putNoDuplicates(map, index, new EventHandler<>(serializer, action));
        }

        public <T> void handleAction(int index, Supplier<? extends NetworkSerializer<T>> supplier, BiConsumer<ServerPlayer, T> action)
        {
            handleAction(index, supplier.get(), action);
        }

        public void handleUnitAction(int index, Consumer<ServerPlayer> action)
        {
            handleAction(index, LimaCoreNetworkSerializers.UNIT, (sender, $) -> action.accept(sender));
        }
    }

    private record EventHandler<T>(NetworkSerializer<T> serializer, BiConsumer<ServerPlayer, T> action)
    { }
}