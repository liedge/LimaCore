package liedge.limacore.menu;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.LimaCore;
import liedge.limacore.menu.slot.FluidMenuInput;
import liedge.limacore.menu.slot.LimaFluidSlot;
import liedge.limacore.menu.slot.LimaItemSlot;
import liedge.limacore.menu.slot.RecipeResultSlot;
import liedge.limacore.network.IndexedStreamData;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.network.packet.ClientboundMenuDataWatcherPacket;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.transfer.LimaTransferUtil;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.limacore.util.LimaCoreObjects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;

@SuppressWarnings("SameParameterValue")
public abstract class LimaMenu<CTX> extends AbstractContainerMenu implements DataWatcherHolder
{
    public static final int DEFAULT_INV_X = 8;
    public static final int DEFAULT_INV_Y = 84;
    public static final int DEFAULT_HOTBAR_Y = 142;
    public static final int DEFAULT_INV_HOTBAR_OFFSET = 58;

    // Base menu properties
    private final LimaMenuType<CTX, ?> type;
    protected final Inventory playerInventory;
    protected final CTX menuContext;
    private final List<LimaDataWatcher<?>> dataWatchers;
    private final Int2ObjectMap<EventHandler<?>> buttonEventHandlers;
    protected final List<LimaFluidSlot> fluidSlots;

    // Convenience menu properties
    private boolean firstTick = true;
    protected int inventoryStart;
    protected int hotbarStart;

    protected LimaMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId);

        this.type = type;
        this.menuContext = menuContext;
        this.playerInventory = inventory;
        this.dataWatchers = createDataWatchers();
        this.fluidSlots = new ObjectArrayList<>();

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
    public void sendDataWatcherPacket(List<IndexedStreamData<?>> streamData)
    {
        getServerUser().connection.send(new ClientboundMenuDataWatcherPacket(streamData, this.containerId));
    }

    @Override
    public boolean stillValid(Player player)
    {
        return type.canPlayerKeepUsing(menuContext, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot.hasItem())
        {
            ItemStack slotStack = slot.getItem();
            clicked = slotStack.copy();

            if (!quickMoveInternal(index, slotStack)) return ItemStack.EMPTY;

            // We have to call onQuickCraft here
            if (slot instanceof RecipeResultSlot)
            {
                slot.onQuickCraft(slotStack, clicked);
            }

            if (slotStack.isEmpty())
            {
                slot.setByPlayer(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }

            if (slotStack.getCount() == clicked.getCount()) return ItemStack.EMPTY;

            slot.onTake(player, slotStack);
        }

        return clicked;
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
        return playerInventory.player.level();
    }

    public List<LimaFluidSlot> getFluidSlots()
    {
        return fluidSlots;
    }

    public void fluidClicked(ServerPlayer player, int slotIndex, FluidMenuInput input)
    {
        LimaFluidSlot slot = fluidSlots.get(slotIndex);
        ResourceHandler<FluidResource> carriedFluids = ItemAccess.forPlayerCursor(player, this).oneByOne().getCapability(Capabilities.Fluid.ITEM);

        if (input == FluidMenuInput.FILL && carriedFluids != null)
        {
            ResourceStack<FluidResource> moved = slot.fillSlotFromItem(carriedFluids);
            if (!LimaTransferUtil.isEmpty(moved))
            {
                SoundEvent sound = moved.resource().getFluidType().getSound(SoundActions.BUCKET_EMPTY);
                if (sound != null) sendSoundToPlayer(player, sound, 1f, 1f);
            }
        }
        else if (input == FluidMenuInput.DRAIN && carriedFluids != null)
        {
            ResourceStack<FluidResource> moved = slot.drainSlotIntoItem(carriedFluids);
            if (!LimaTransferUtil.isEmpty(moved))
            {
                SoundEvent sound = moved.resource().getFluidType().getSound(SoundActions.BUCKET_FILL);
                if (sound != null) sendSoundToPlayer(player, sound, 1f, 1f);
            }
        }
        else if (input == FluidMenuInput.CLONE && getCarried().isEmpty() && slot.canCreateCloneBucket(player))
        {
            FluidResource resource = slot.getFluidResource();
            ItemStack bucket = resource.getFluid().getBucket().getDefaultInstance();

            if (!bucket.isEmpty())
            {
                setCarried(bucket);
                SoundEvent sound = resource.getFluidType().getSound(SoundActions.BUCKET_FILL);
                if (sound != null) sendSoundToPlayer(player, sound, 1f, 1f);
            }
        }
        else if (input == FluidMenuInput.CLEAR && !getCarried().isEmpty() && slot.canClear(player, getCarried()))
        {
            slot.clearFluid(player);
        }
    }

    public ServerPlayer getServerUser()
    {
        return LimaCoreObjects.cast(ServerPlayer.class, playerInventory.player, "Attempted to access server menu user on client.");
    }

    public void sendSoundToPlayer(ServerPlayer player, Holder<SoundEvent> sound, float volume, float pitch)
    {
        Vec3 pos = player.position();
        ClientboundSoundPacket packet = new ClientboundSoundPacket(sound, SoundSource.PLAYERS, pos.x, pos.y, pos.z, volume, pitch, player.getRandom().nextLong());
        player.connection.send(packet);
    }

    public void sendSoundToPlayer(ServerPlayer player, SoundEvent sound, float volume, float pitch)
    {
        sendSoundToPlayer(player, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), volume, pitch);
    }

    @ApiStatus.Internal
    public final void handleCustomButtonData(ServerPlayer sender, IndexedStreamData<?> streamData)
    {
        int buttonId = streamData.index();

        if (buttonEventHandlers.containsKey(buttonId))
        {
            EventHandler<?> handler = buttonEventHandlers.get(buttonId);
            handler.tryHandle(sender, streamData);
        }
        else
        {
            LimaCore.LOGGER.warn("Received custom button data with invalid ID {}", buttonId);
        }
    }

    //#region Quick move functions
    protected boolean quickMoveInternal(int index, ItemStack stack)
    {
        if (index < inventoryStart && slots.get(index) instanceof LimaItemSlot limaSlot)
        {
            // Transfer from container slots to inventory.
            return quickMoveToAllInventory(stack, limaSlot.reverseQuickTransfer());
        }
        else if (index >= inventoryStart)
        {
            // Transfer from inventory to container
            return quickMoveToContainer(stack);
        }
        else
        {
            return false; // No op for invalid edge cases.
        }
    }

    protected boolean quickMoveToContainer(ItemStack stack)
    {
        boolean result = false;

        // First try to fill existing slots
        if (stack.isStackable())
        {
            for (int i = 0; i < inventoryStart; i++)
            {
                if (slots.get(i) instanceof LimaItemSlot limaSlot)
                {
                    if (!limaSlot.canQuickTransfer(stack)) continue;

                    ItemStack slotStack = limaSlot.getItem();
                    if (ItemStack.isSameItemSameComponents(stack, slotStack))
                    {
                        int n = slotStack.getCount() + stack.getCount();
                        int maxStackSize = limaSlot.getMaxStackSize(slotStack);

                        if (n <= maxStackSize)
                        {
                            stack.setCount(0);
                            slotStack.setCount(n);
                            limaSlot.setChanged();
                            result = true;
                        }
                        else if (slotStack.getCount() < maxStackSize)
                        {
                            stack.shrink(maxStackSize - slotStack.getCount());
                            slotStack.setCount(maxStackSize);
                            limaSlot.setChanged();
                            result = true;
                        }
                    }
                }

                if (stack.isEmpty()) break;
            }
        }

        // Insert remaining stack (if any) into the next empty slot
        if (!stack.isEmpty())
        {
            for (int i = 0; i < inventoryStart; i++)
            {
                if (slots.get(i) instanceof LimaItemSlot limaSlot)
                {
                    if (limaSlot.getItem().isEmpty() && limaSlot.canQuickTransfer(stack))
                    {
                        int maxStackSize = limaSlot.getMaxStackSize(stack);
                        limaSlot.setByPlayer(stack.split(Math.min(stack.getCount(), maxStackSize)));
                        limaSlot.setChanged();
                        result = true;
                        break;
                    }
                }
            }
        }

        return result;
    }

    protected boolean quickMoveToInventory(ItemStack stack, boolean reverse)
    {
        return moveItemStackTo(stack, inventoryStart, inventoryStart + 27, reverse);
    }

    protected boolean quickMoveToHotbar(ItemStack stack, boolean reverse)
    {
        return moveItemStackTo(stack, hotbarStart, hotbarStart + 9, reverse);
    }

    protected boolean quickMoveToAllInventory(ItemStack stack, boolean reverse)
    {
        return quickMoveToInventory(stack, reverse) || quickMoveToHotbar(stack, reverse);
    }
    //#endregion

    protected void runSlotsGrid(int startIndex, int xPos, int yPos, int columns, int rows, SlotPosConsumer consumer)
    {
        for (int y = 0; y < rows; y++)
        {
            for (int x = 0; x < columns; x++)
            {
                consumer.accept(startIndex + (columns * y + x), xPos + x * 18, yPos + y * 18);
            }
        }
    }

    protected <T> void addSlotsGrid(T container, int startIndex, int xPos, int yPos, int width, int height, ItemSlotFactory<? super T> factory)
    {
        runSlotsGrid(startIndex, xPos, yPos, width, height, (resourceIndex, slotX, slotY) -> addSlot(factory.create(container, resourceIndex, slotX, slotY)));
    }

    protected void addPlayerInventory(int xPos, int yPos, ItemSlotFactory<Container> factory)
    {
        inventoryStart = slots.size();
        addSlotsGrid(playerInventory, 9, xPos, yPos, 9, 3, factory);
    }

    protected void addPlayerInventory(int xPos, int yPos)
    {
        addPlayerInventory(xPos, yPos, Slot::new);
    }

    protected void addPlayerHotbar(int xPos, int yPos, ItemSlotFactory<Container> factory)
    {
        hotbarStart = slots.size();
        addSlotsGrid(playerInventory, 0, xPos, yPos, 9, 1, factory);
    }

    protected void addPlayerHotbar(int xPos, int yPos)
    {
        addPlayerHotbar(xPos, yPos, Slot::new);
    }

    protected void addPlayerInventoryAndHotbar(int xPos, int yPos)
    {
        addPlayerInventory(xPos, yPos);
        addPlayerHotbar(xPos, yPos + DEFAULT_INV_HOTBAR_OFFSET);
    }

    protected void addDefaultPlayerInventoryAndHotbar()
    {
        addPlayerInventoryAndHotbar(DEFAULT_INV_X, DEFAULT_INV_Y);
    }

    protected void addFluidSlot(IntFunction<LimaFluidSlot> indexFunction)
    {
        fluidSlots.add(indexFunction.apply(fluidSlots.size()));
    }

    protected <T> void addFluidSlotsGrid(T container, int startIndex, int xPos, int yPos, int width, int height, FluidSlotFactory<? super T> factory)
    {
        runSlotsGrid(startIndex, xPos, yPos, width, height, (resourceIndex, slotX, slotY) ->
        {
            int slotIndex = fluidSlots.size();
            fluidSlots.add(factory.create(container, slotIndex, slotX, slotY, resourceIndex));
        });
    }

    @FunctionalInterface
    public interface SlotPosConsumer
    {
        void accept(int resourceIndex, int slotX, int slotY);
    }

    @FunctionalInterface
    public interface ItemSlotFactory<T>
    {
        Slot create(T container, int resourceIndex, int slotX, int slotY);
    }

    @FunctionalInterface
    public interface FluidSlotFactory<T>
    {
        LimaFluidSlot create(T container, int slotIndex, int slotX, int slotY, int resourceIndex);
    }

    protected static class EventHandlerBuilder
    {
        private @Nullable Int2ObjectMap<EventHandler<?>> map;

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
            handleAction(index, LimaCoreNetworkSerializers.UNIT, (sender, _) -> action.accept(sender));
        }
    }

    private record EventHandler<T>(NetworkSerializer<T> serializer, BiConsumer<ServerPlayer, T> action)
    {
        private void tryHandle(ServerPlayer player, IndexedStreamData<?> streamData)
        {
            T data = streamData.tryCast(serializer);
            if (data != null) action.accept(player, data);
        }
    }
}