package liedge.limacore.capability.itemhandler;

import liedge.limacore.blockentity.IOAccess;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public interface ItemHandlerIOWrapper extends IItemHandlerModifiable
{
    static ItemHandlerIOWrapper of(IItemHandlerModifiable handler, IOAccess access)
    {
        return new SimpleWrapper(handler, access);
    }

    IItemHandlerModifiable itemHandler();

    boolean allowInput(int slot);

    boolean allowOutput(int slot);

    @Override
    default int getSlots()
    {
        return itemHandler().getSlots();
    }

    @Override
    default void setStackInSlot(int slot, ItemStack stack)
    {
        itemHandler().setStackInSlot(slot, stack);
    }

    @Override
    default ItemStack getStackInSlot(int slot)
    {
        return itemHandler().getStackInSlot(slot);
    }

    @Override
    default ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        if (allowInput(slot))
            return itemHandler().insertItem(slot, stack, simulate);
        else
            return stack;
    }

    @Override
    default ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (allowOutput(slot))
            return itemHandler().extractItem(slot, amount, simulate);
        else
            return ItemStack.EMPTY;
    }

    @Override
    default int getSlotLimit(int slot)
    {
        return itemHandler().getSlotLimit(slot);
    }

    @Override
    default boolean isItemValid(int slot, ItemStack stack)
    {
        return itemHandler().isItemValid(slot, stack);
    }

    record SimpleWrapper(IItemHandlerModifiable itemHandler, IOAccess access) implements ItemHandlerIOWrapper
    {
        @Override
        public boolean allowInput(int slot)
        {
            return access.allowsInput();
        }

        @Override
        public boolean allowOutput(int slot)
        {
            return access.allowsOutput();
        }
    }
}