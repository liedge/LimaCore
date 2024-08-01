package liedge.limacore.inventory.menu;

import liedge.limacore.inventory.slot.RecipeResultSlot;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.network.packet.ClientboundMenuDataPacket;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.util.LimaCoreUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class LimaMenu<CTX> extends AbstractContainerMenu implements DataWatcherHolder
{
    public static final int DEFAULT_INV_X = 8;
    public static final int DEFAULT_INV_Y = 84;
    public static final int DEFAULT_HOTBAR_Y = 142;

    // Commonly used menu properties
    private final LimaMenuType<CTX, ?> type;
    private final List<LimaDataWatcher<?>> dataWatchers;
    protected final Inventory playerInventory;
    private final CTX menuContext;
    private final Level level;
    private final ServerPlayer user;
    private boolean firstTick = true;

    private int inventoryStart;
    private int inventoryEnd;
    private int hotbarStart;
    private int hotbarEnd;

    protected LimaMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId);

        this.type = type;
        this.dataWatchers = defineDataWatchers(menuContext);
        this.playerInventory = inventory;
        this.menuContext = menuContext;
        this.level = playerInventory.player.level();
        this.user = LimaCoreUtil.castOrNull(ServerPlayer.class, playerInventory.player);
    }

    @Override
    public final List<LimaDataWatcher<?>> getDataWatchers()
    {
        return dataWatchers;
    }

    @Override
    public <T> void sendDataWatcherPacket(int index, NetworkSerializer<T> streamCodec, T data)
    {
        getUser().connection.send(new ClientboundMenuDataPacket<>(this.containerId, index, streamCodec, data));
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
            }
        }

        return stack;
    }

    @Override
    public final void broadcastChanges()
    {
        super.broadcastChanges();
        broadcastChanges(firstTick);
        if (firstTick) firstTick = false;
    }

    /**
     * Defines the data watchers used for this menu, called during construction.
     * @param menuContext Subclasses should use this parameter if the context is needed. Do not use the instance's getter method, it will return null at this stage of menu initialization.
     */
    protected List<LimaDataWatcher<?>> defineDataWatchers(CTX menuContext)
    {
        return List.of();
    }

    protected void broadcastChanges(boolean isFirstTick)
    {
        tickDataWatchers(isFirstTick);
    }

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

    public void handleCustomButton(ServerPlayer sender, int buttonId, int value) {}

    //#region Quick move functions
    protected abstract boolean quickMoveInternal(int index, ItemStack stack);

    protected boolean quickMoveToSlot(ItemStack stack, int slot, boolean reverse)
    {
        return moveItemStackTo(stack, slot, slot + 1, reverse);
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

    protected <T extends IItemHandler> void addItemHandlerSlotGrid(T itemHandler, int startIndex, int xPos, int yPos, int width, int height, MenuSlotFactory<? super T> factory)
    {
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                addSlot(factory.createSlot(itemHandler, startIndex + (width * y + x), xPos + x * 18, yPos + y * 18));
            }
        }
    }

    protected void addRecipeResultSlot(IItemHandlerModifiable handler, int index, int x, int y, RecipeType<?> recipeType)
    {
        addSlot(new RecipeResultSlot(playerInventory.player, recipeType, handler, index, x, y));
    }

    protected void addRecipeResultSlot(IItemHandlerModifiable handler, int index, int x, int y, Supplier<? extends RecipeType<?>> recipeTypeSupplier)
    {
        addRecipeResultSlot(handler, index, x, y, recipeTypeSupplier.get());
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
}