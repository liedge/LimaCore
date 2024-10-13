package liedge.limacore.capability.itemhandler;

import liedge.limacore.blockentity.IOAccess;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.function.IntPredicate;

public class ItemHandlerIOWrapper implements IItemHandler
{
    public static ItemHandlerIOWrapper create(IItemHandler parent)
    {
        if (parent instanceof LimaItemHandlerBase limaHandler)
        {
            return new ItemHandlerIOWrapper(parent, slot -> limaHandler.getSlotIOAccess(slot).allowsInput(), slot -> limaHandler.getSlotIOAccess(slot).allowsOutput());
        }
        else
        {
            return create(parent, IOAccess.INPUT_AND_OUTPUT);
        }
    }

    public static ItemHandlerIOWrapper create(IItemHandler parent, IOAccess access)
    {
        return new ItemHandlerIOWrapper(parent, $ -> access.allowsInput(), $ -> access.allowsOutput());
    }

    private final IItemHandler parent;
    private final IntPredicate insertionValidator;
    private final IntPredicate extractionValidator;

    private ItemHandlerIOWrapper(IItemHandler parent, IntPredicate insertionValidator, IntPredicate extractionValidator)
    {
        this.parent = parent;
        this.insertionValidator = insertionValidator;
        this.extractionValidator = extractionValidator;
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
        if (insertionValidator.test(slot))
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
        if (extractionValidator.test(slot))
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