package liedge.limacore.menu.slot;

import liedge.limacore.capability.itemhandler.LimaItemHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;

public class LimaHandlerSlot extends SlotItemHandler
{
    protected final IItemHandlerModifiable itemHandler;

    private final boolean allowInsert;

    public LimaHandlerSlot(IItemHandlerModifiable itemHandler, int slotIndex, int xPos, int yPos, boolean allowInsert)
    {
        super(itemHandler, slotIndex, xPos, yPos);
        this.itemHandler = itemHandler;
        this.allowInsert = allowInsert;
    }

    public LimaHandlerSlot(IItemHandlerModifiable itemHandler, int slotIndex, int xPos, int yPos)
    {
        this(itemHandler, slotIndex, xPos, yPos, true);
    }

    @Override
    public int getMaxStackSize()
    {
        return itemHandler.getSlotLimit(index);
    }

    @Override
    public int getMaxStackSize(ItemStack stack)
    {
        return Math.min(getMaxStackSize(), stack.getMaxStackSize());
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return allowInsert && itemHandler.isItemValid(index, stack);
    }

    @Override
    public IItemHandlerModifiable getItemHandler()
    {
        return itemHandler;
    }

    @Override
    public void setByPlayer(ItemStack stack)
    {
        super.setByPlayer(stack);
    }

    public void setBaseContainerChanged()
    {
        if (itemHandler instanceof LimaItemHandler limaHandler) limaHandler.onContentsChanged(index);
    }
}