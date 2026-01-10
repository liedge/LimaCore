package liedge.limacore.capability.itemhandler;

import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.util.LimaItemUtil;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;

import java.util.function.Predicate;

public final class LimaItemHandlerUtil
{
    private LimaItemHandlerUtil() {}

    public static RangedWrapper sizedRangedWrapper(IItemHandlerModifiable source, int startSlot, int slotCount)
    {
        return new RangedWrapper(source, startSlot, startSlot + slotCount);
    }

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
        LimaCoreMath.validateOpenIndexRange(slotStartInclusive, slotEndExclusive, destination.getSlots());
        if (toInsert.isEmpty()) return toInsert;

        for (int i = slotStartInclusive; i < slotEndExclusive; i++)
        {
            toInsert = destination.insertItem(i, toInsert, simulate);
            if (toInsert.isEmpty()) return ItemStack.EMPTY;
        }

        return toInsert;
    }

    public static void transferItemsBetween(IItemHandler source, IItemHandler destination, int sourceSlotStart, int sourceSlotEnd, Predicate<ItemStack> predicate)
    {
        LimaCoreMath.validateOpenIndexRange(sourceSlotStart, sourceSlotEnd, source.getSlots());

        for (int i = sourceSlotStart; i < sourceSlotEnd; i++)
        {
            ItemStack sourceStack = source.extractItem(i, source.getSlotLimit(i), true);
            if (sourceStack.isEmpty() || !predicate.test(sourceStack)) continue;

            ItemStack inserted = ItemHandlerHelper.insertItem(destination, sourceStack, false);
            int insertCount = sourceStack.getCount() - inserted.getCount();
            if (insertCount > 0) source.extractItem(i, insertCount, false);
        }
    }

    public static void transferItemsBetween(IItemHandler source, IItemHandler destination, Predicate<ItemStack> predicate)
    {
        transferItemsBetween(source, destination, 0, source.getSlots(), predicate);
    }

    public static void transferItemsBetween(IItemHandler source, IItemHandler destination)
    {
        transferItemsBetween(source, destination, LimaItemUtil.ALWAYS_TRUE);
    }
}