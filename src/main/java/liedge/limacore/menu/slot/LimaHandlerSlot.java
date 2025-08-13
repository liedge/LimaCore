package liedge.limacore.menu.slot;

import liedge.limacore.capability.itemhandler.LimaItemHandler;
import liedge.limacore.util.LimaItemUtil;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.function.Predicate;

public class LimaHandlerSlot extends SlotItemHandler
{
    private final boolean allowInsert;
    private final Predicate<ItemStack> quickTransferPredicate;

    public LimaHandlerSlot(IItemHandlerModifiable itemHandler, int handlerIndex, int xPos, int yPos, boolean allowInsert, Predicate<ItemStack> quickTransferPredicate)
    {
        super(itemHandler, handlerIndex, xPos, yPos);
        this.allowInsert = allowInsert;
        this.quickTransferPredicate = quickTransferPredicate;
    }

    public LimaHandlerSlot(IItemHandlerModifiable itemHandler, int handlerIndex, int xPos, int yPos)
    {
        this(itemHandler, handlerIndex, xPos, yPos, true, LimaItemUtil.ALWAYS_TRUE);
    }

    public boolean reverseQuickTransfer()
    {
        return false;
    }

    public boolean canQuickTransfer(ItemStack stack)
    {
        return quickTransferPredicate.test(stack);
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return allowInsert && getItemHandler().isItemValid(index, stack);
    }

    public void setBaseContainerChanged()
    {
        if (getItemHandler() instanceof LimaItemHandler limaHandler) limaHandler.onContentsChanged(index);
    }
}