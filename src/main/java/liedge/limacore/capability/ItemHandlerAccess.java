package liedge.limacore.capability;

import liedge.limacore.capability.itemhandler.LimaItemHandlerBase;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class ItemHandlerAccess implements IItemHandler
{
    private final LimaItemHandlerBase source;

    public ItemHandlerAccess(LimaItemHandlerBase source)
    {
        this.source = source;
    }

    @Override
    public int getSlots()
    {
        return source.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return source.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        if (source.getSlotIOAccess(slot).isInputEnabled())
        {
            return source.insertItem(slot, stack, simulate);
        }
        else
        {
            return stack;
        }
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (source.getSlotIOAccess(slot).isOutputEnabled())
        {
            return source.extractItem(slot, amount, simulate);
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return source.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return source.isItemValid(slot, stack);
    }
}