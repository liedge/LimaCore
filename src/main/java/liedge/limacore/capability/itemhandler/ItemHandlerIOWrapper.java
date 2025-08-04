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

    IItemHandlerModifiable source();

    boolean allowInput(int slot);

    boolean allowOutput(int slot);

    @Override
    default int getSlots()
    {
        return source().getSlots();
    }

    @Override
    default void setStackInSlot(int slot, ItemStack stack)
    {
        source().setStackInSlot(slot, stack);
    }

    @Override
    default ItemStack getStackInSlot(int slot)
    {
        return source().getStackInSlot(slot);
    }

    @Override
    default ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        if (allowInput(slot))
            return source().insertItem(slot, stack, simulate);
        else
            return stack;
    }

    @Override
    default ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (allowOutput(slot))
            return source().extractItem(slot, amount, simulate);
        else
            return ItemStack.EMPTY;
    }

    @Override
    default int getSlotLimit(int slot)
    {
        return source().getSlotLimit(slot);
    }

    @Override
    default boolean isItemValid(int slot, ItemStack stack)
    {
        return source().isItemValid(slot, stack);
    }

    record SimpleWrapper(IItemHandlerModifiable source, IOAccess access) implements ItemHandlerIOWrapper
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