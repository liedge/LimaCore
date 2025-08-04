package liedge.limacore.capability.itemhandler;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntityAccess;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public interface ItemHolderBlockEntity extends LimaBlockEntityAccess
{
    /**
     * Tries to retrieve the {@link LimaBlockEntityItemHandler} associated with the given {@link BlockInventoryType}. Block inventory
     * types are arbitrary inventory classification is handled by the implementing class. Do not assume guaranteed access
     * to any particular type.
     * @param inventoryType The inventory type of the item handler.
     * @return The item handler for the given type, or {@code null} if none exists.
     * @apiNote Unless absolutely necessary, use {@link ItemHolderBlockEntity#getItemHandlerOrThrow(BlockInventoryType)} (BlockInventoryType)} for
     * proper access enforcement.
     */
    @Nullable LimaBlockEntityItemHandler getItemHandler(BlockInventoryType inventoryType);

    /**
     * Retrieves the {@link LimaBlockEntityItemHandler} associated with the given {@link BlockInventoryType}, or throws
     * an exception if none exists.
     * @param inventoryType The inventory type of item handler.
     * @return The item handler for the given type
     * @throws IllegalArgumentException If the block entity does not support the given type.
     */
    default LimaBlockEntityItemHandler getItemHandlerOrThrow(BlockInventoryType inventoryType)
    {
        LimaBlockEntityItemHandler handler = getItemHandler(inventoryType);
        if (handler != null)
            return handler;
        else
            throw new IllegalArgumentException("Block entity does not support inventory type: " + inventoryType.getSerializedName());
    }

    /**
     * Checks if an {@link ItemStack} is valid for a specific slot in the specified item handler.
     * Called by {@link LimaBlockEntityItemHandler#isItemValid(int, ItemStack)} using its handler index.
     * @param inventoryType The inventory type of the item handler calling this function.
     * @param slot The item handler slot to be checked
     * @param stack The item stack to be checked
     * @return If the item is valid.
     */
    @ApiStatus.OverrideOnly
    boolean isItemValid(BlockInventoryType inventoryType, int slot, ItemStack stack);

    /**
     * Called by {@link LimaBlockEntityItemHandler#onContentsChanged(int)} whenever a slot's contents are modified.
     * Default implementation marks the block entity as changed.
     * @param inventoryType The inventory type of the handler that changed.
     * @param slot The slot of the item handler that changed.
     */
    @ApiStatus.OverrideOnly
    default void onItemSlotChanged(BlockInventoryType inventoryType, int slot)
    {
        setChanged();
    }

    /**
     * Declares the {@link IOAccess} permission for the specified side. This is the top level permission and applies
     * to all of this block entity's item handlers.
     * @param side The side to check.
     * @return The IO access permission for the block entity's side.
     */
    IOAccess getSideIOForItems(@Nullable Direction side);

    /**
     * Gets the {@link IOAccess} permission at the slot level for an individual item handler. The default implementation
     * provides all-purpose defaults. This method is only used for wrappers created by
     * {@link LimaBlockEntityItemHandler#createIOWrapper(IOAccess)}.
     * @param type The inventory type of the item handler.
     * @param slot The slot index.
     * @return The slot's IO permission level.
     */
    @ApiStatus.OverrideOnly
    default IOAccess getItemHandlerSlotIO(BlockInventoryType type, int slot)
    {
        return switch (type)
        {
            case GENERAL -> IOAccess.INPUT_AND_OUTPUT;
            case AUXILIARY -> IOAccess.DISABLED;
            case INPUT -> IOAccess.INPUT_ONLY;
            case OUTPUT -> IOAccess.OUTPUT_ONLY;
        };
    }

    /**
     * Called by {@link LimaBlockEntityItemHandler#onLoad()} whenever it is loaded, using its handler index.
     * @param inventoryType The item handler's inventory type.
     */
    @ApiStatus.OverrideOnly
    default void onItemHandlerLoaded(BlockInventoryType inventoryType) {}

    // Capability helper objects

    /**
     * Creates, if compatible, a {@link ItemHandlerIOWrapper} for the given side. Preferably, centralize the creation of
     * capability objects for {@link RegisterCapabilitiesEvent} using this method.
     * @param side The side to create a capability wrapper for.
     * @return The item handler wrapper, or {@code null} if not compatible.
     */
    @Nullable IItemHandler createItemIOWrapper(@Nullable Direction side);

    @ApiStatus.NonExtendable
    default @Nullable IItemHandler wrapInputOutputInventories(@Nullable Direction side)
    {
        IOAccess blockAccessLevel = getSideIOForItems(side);
        return switch (blockAccessLevel)
        {
            case DISABLED -> null;
            case INPUT_ONLY -> getItemHandlerOrThrow(BlockInventoryType.INPUT).createIOWrapper(blockAccessLevel);
            case OUTPUT_ONLY -> getItemHandlerOrThrow(BlockInventoryType.OUTPUT).createIOWrapper(blockAccessLevel);
            case INPUT_AND_OUTPUT -> new CombinedInvWrapper(getItemHandlerOrThrow(BlockInventoryType.INPUT).createIOWrapper(blockAccessLevel), getItemHandlerOrThrow(BlockInventoryType.OUTPUT).createIOWrapper(blockAccessLevel));
        };
    }
}