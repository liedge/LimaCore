package liedge.limacore.capability.itemhandler;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public final class LimaItemHandlerUtil
{
    private LimaItemHandlerUtil() {}

    public static int getNextEmptySlot(IItemHandler handler, boolean reverse)
    {
        int i = reverse ? handler.getSlots() - 1 : 0;
        final int step = reverse ? -1 : 1;

        while (reverse ? i >= 0 : i < handler.getSlots())
        {
            if (handler.getStackInSlot(i).isEmpty()) return i;
            i += step;
        }

        return -1;
    }

    public static int getNextEmptySlot(IItemHandler handler)
    {
        return getNextEmptySlot(handler, false);
    }

    public static ItemStack extractFromAnySlot(IItemHandler source, int amount, boolean simulate)
    {
        for (int i = 0; i < source.getSlots(); i++)
        {
            ItemStack existingStack = source.getStackInSlot(i);
            if (!existingStack.isEmpty())
            {
                return source.extractItem(i, amount, simulate);
            }
        }

        return ItemStack.EMPTY;
    }

    public static ItemStack insertIntoAnySlot(IItemHandler destination, ItemStack toInsert, boolean simulate)
    {
        for (int i = 0; i < destination.getSlots(); i++)
        {
            ItemStack inserted = destination.insertItem(i, toInsert, simulate);
            if (inserted != toInsert) return inserted;
        }

        return toInsert;
    }
}