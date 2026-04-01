package liedge.limacore.menu.slot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public class LimaItemSlot extends ResourceHandlerSlot
{
    public static LimaItemSlot create(ItemStacksResourceHandler handler, int index, int xPosition, int yPosition)
    {
        return new LimaItemSlot(handler, index, xPosition, yPosition);
    }

    protected boolean allowPlace = true;
    private @Nullable Predicate<ItemStack> quickTransferTest;

    public LimaItemSlot(ItemStacksResourceHandler handler, int index, int xPosition, int yPosition)
    {
        super(handler, handler::set, index, xPosition, yPosition);
    }

    public LimaItemSlot allowPlacement(boolean allowPlace)
    {
        this.allowPlace = allowPlace;
        return this;
    }

    public LimaItemSlot setQuickTransferTest(Predicate<ItemStack> quickTransferTest)
    {
        this.quickTransferTest = quickTransferTest;
        return this;
    }

    public boolean reverseQuickTransfer()
    {
        return false;
    }

    public boolean canQuickTransfer(ItemStack stack)
    {
        return mayPlace(stack) && (quickTransferTest == null || quickTransferTest.test(stack));
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return allowPlace && super.mayPlace(stack);
    }
}