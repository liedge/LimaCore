package liedge.limacore.inventory.menu;

import liedge.limacore.capability.itemhandler.LimaItemHandlerBase;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;

public class LimaItemHandlerMenuSlot extends SlotItemHandler
{
    protected final IItemHandlerModifiable itemHandler;
    protected final int slotIndex;

    private final boolean allowInsert;

    public LimaItemHandlerMenuSlot(IItemHandlerModifiable itemHandler, int slotIndex, int xPos, int yPos, boolean allowInsert)
    {
        super(itemHandler, slotIndex, xPos, yPos);
        this.slotIndex = slotIndex;
        this.itemHandler = itemHandler;
        this.allowInsert = allowInsert;
    }

    public LimaItemHandlerMenuSlot(IItemHandlerModifiable itemHandler, int slotIndex, int xPos, int yPos)
    {
        this(itemHandler, slotIndex, xPos, yPos, true);
    }

    @Override
    public int getMaxStackSize()
    {
        return itemHandler.getSlotLimit(slotIndex);
    }

    @Override
    public int getMaxStackSize(ItemStack stack)
    {
        return Math.min(getMaxStackSize(), stack.getMaxStackSize());
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return allowInsert && itemHandler.isItemValid(slotIndex, stack);
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
        if (itemHandler instanceof LimaItemHandlerBase limaHandler) limaHandler.onContentsChanged(index);
    }
}