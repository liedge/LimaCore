package liedge.limacore.capability.itemhandler;

import liedge.limacore.util.LimaMathUtil;
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

    /**
     * A modified version of {@link net.neoforged.neoforge.items.ItemHandlerHelper#insertItem(IItemHandler, ItemStack, boolean)}
     * which only iterates over an open range of slot indices.
     * @param destination The destination item handler
     * @param toInsert The stack to insert
     * @param slotStartInclusive The start slot index (inclusive)
     * @param slotEndExclusive The end slot index (exclusive/open-ended)
     * @param simulate Whether to simulate the insertion
     * @return An empty stack if {@code toInsert} was fully inserted. Otherwise, returns the remainder item stack.
     */
    public static ItemStack insertItemIntoSlots(IItemHandler destination, ItemStack toInsert, int slotStartInclusive, int slotEndExclusive, boolean simulate)
    {
        LimaMathUtil.validateOpenIndexRange(slotStartInclusive, slotEndExclusive, destination.getSlots());
        if (toInsert.isEmpty()) return toInsert;

        for (int i = slotStartInclusive; i < slotEndExclusive; i++)
        {
            toInsert = destination.insertItem(i, toInsert, simulate);
            if (toInsert.isEmpty()) return ItemStack.EMPTY;
        }

        return toInsert;
    }

    public static void transferStackBetweenInventories(IItemHandler source, IItemHandler destination, int sourceSlot)
    {
        if (source.getStackInSlot(sourceSlot).isEmpty()) return;

        for (int i = 0; i < destination.getSlots(); i++)
        {
            ItemStack sourceItem = source.getStackInSlot(sourceSlot);
            ItemStack inserted = destination.insertItem(i, sourceItem, true);

            int insertCount = sourceItem.getCount() - inserted.getCount();
            if (insertCount > 0) destination.insertItem(i, source.extractItem(sourceSlot, insertCount, false), false);

            if (source.getStackInSlot(sourceSlot).isEmpty() || inserted.isEmpty()) break;
        }
    }
}