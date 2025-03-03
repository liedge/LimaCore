package liedge.limacore.capability.itemhandler;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntityAccess;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public interface ItemHolderBlockEntity extends LimaBlockEntityAccess
{
    /**
     * Retrieves the {@link LimaItemHandlerBase} associated with the given {@code handlerIndex}. Implementations of this
     * interface must always return a valid item handler for index {@code 0}, the primary item handler. Additional item handlers
     * do not have to be exposed.
     * @param handlerIndex The item handler index. Must be non-negative.
     * @return The item handler at the given index.
     * @throws IndexOutOfBoundsException If the provided index is out of range for this block entity's item handlers.
     */
    LimaItemHandlerBase getItemHandler(int handlerIndex) throws IndexOutOfBoundsException;

    /**
     * Retrieves the primary {@link LimaItemHandlerBase} for this block entity at index {@code 0}.
     * @return The primary item handler.
     */
    default LimaItemHandlerBase getItemHandler()
    {
        return getItemHandler(0);
    }

    /**
     * Checks if an {@link ItemStack} is valid for a specific slot in the specified item handler.
     * Called by {@link LimaBlockEntityItemHandler#isItemValid(int, ItemStack)} using its handler index.
     * @param handlerIndex The index of the item handler calling this function.
     * @param slot The item handler slot to be checked
     * @param stack The item stack to be checked
     * @return If the item is valid.
     */
    @ApiStatus.OverrideOnly
    boolean isItemValid(int handlerIndex, int slot, ItemStack stack);

    /**
     * Called by {@link LimaBlockEntityItemHandler#onContentsChanged(int)} whenever a slot's contents are modified.
     * Default implementation marks the block entity as changed.
     * @param handlerIndex The index of the item handler that changed.
     * @param slot The slot of the item handler that changed.
     */
    @ApiStatus.OverrideOnly
    default void onItemSlotChanged(int handlerIndex, int slot)
    {
        setChanged();
    }

    /**
     * Retrieves the {@link IOAccess} level of the block entity for the specified side.
     * @param side The side to check
     * @return The IO access level
     */
    IOAccess getItemHandlerSideIO(Direction side);

    default IOAccess getItemSlotIO(int handlerIndex, int slot)
    {
        return IOAccess.INPUT_AND_OUTPUT;
    }

    /**
     * Called by {@link LimaBlockEntityItemHandler#onLoad()} whenever it is loaded, using its handler index.
     * @param handlerIndex The index of the item handler calling this function.
     */
    @ApiStatus.OverrideOnly
    default void onItemHandlerLoaded(int handlerIndex) {}

    default @Nullable IItemHandler createItemIOWrapper(int handlerIndex, @Nullable Direction side)
    {
        if (side != null) return new ItemHandlerIOWrapper(getItemHandler(handlerIndex), getItemHandlerSideIO(side));

        return null;
    }

    default @Nullable IItemHandler createItemIOWrapper(@Nullable Direction side)
    {
        return createItemIOWrapper(0, side);
    }
}