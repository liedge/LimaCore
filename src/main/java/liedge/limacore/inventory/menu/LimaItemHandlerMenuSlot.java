package liedge.limacore.inventory.menu;

import net.minecraft.world.entity.player.Player;
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
    public boolean mayPickup(Player playerIn)
    {
        return !itemHandler.extractItem(slotIndex, 1, true).isEmpty();
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return allowInsert && itemHandler.isItemValid(slotIndex, stack);
    }

    @Override
    public ItemStack remove(int amount)
    {
        return itemHandler.extractItem(slotIndex, amount, false);
    }

    @Override
    public IItemHandlerModifiable getItemHandler()
    {
        return itemHandler;
    }
}