package liedge.limacore.menu.slot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class LimaItemSlot extends ResourceHandlerSlot
{
    private final boolean allowPlace;

    public LimaItemSlot(ItemStacksResourceHandler handler, int index, int xPosition, int yPosition, boolean allowPlace)
    {
        super(handler, handler::set, index, xPosition, yPosition);
        this.allowPlace = allowPlace;
    }

    public LimaItemSlot(ItemStacksResourceHandler handler, int index, int xPosition, int yPosition)
    {
        this(handler, index, xPosition, yPosition, true);
    }

    public boolean reverseQuickTransfer()
    {
        return false;
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return allowPlace && super.mayPlace(stack);
    }
}