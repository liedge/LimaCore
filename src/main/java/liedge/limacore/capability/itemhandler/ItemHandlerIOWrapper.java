package liedge.limacore.capability.itemhandler;

import liedge.limacore.blockentity.IOAccess;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class ItemHandlerIOWrapper implements IItemHandler
{
    private final LimaItemHandlerBase parent;
    private final IOAccess ioAccess;

    public ItemHandlerIOWrapper(LimaItemHandlerBase parent, IOAccess ioAccess)
    {
        this.parent = parent;
        this.ioAccess = ioAccess;
    }

    @Override
    public int getSlots()
    {
        return parent.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return parent.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        if (parent.getSlotIOAccess(slot).allowsInput() && ioAccess.allowsInput())
        {
            return parent.insertItem(slot, stack, simulate);
        }
        else
        {
            return stack;
        }
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (parent.getSlotIOAccess(slot).allowsOutput() && ioAccess.allowsOutput())
        {
            return parent.extractItem(slot, amount, simulate);
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return parent.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return parent.isItemValid(slot, stack);
    }
}