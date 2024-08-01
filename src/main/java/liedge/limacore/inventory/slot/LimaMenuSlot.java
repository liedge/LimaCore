package liedge.limacore.inventory.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;

public class LimaMenuSlot extends SlotItemHandler
{
    protected final IItemHandlerModifiable handler;
    protected final boolean allowPlace;
    protected final int slotIndex;

    public LimaMenuSlot(IItemHandlerModifiable handler, boolean allowPlace, int slotIndex, int x, int y)
    {
        super(handler, slotIndex, x, y);
        this.handler = handler;
        this.allowPlace = allowPlace;
        this.slotIndex = slotIndex;
    }

    public LimaMenuSlot(IItemHandlerModifiable handler, int slotIndex, int x, int y)
    {
        this(handler, true, slotIndex, x, y);
    }

    @Override
    public IItemHandlerModifiable getItemHandler()
    {
        return handler;
    }

    @Override
    public int getMaxStackSize()
    {
        return handler.getSlotLimit(slotIndex);
    }

    @Override
    public int getMaxStackSize(ItemStack stack)
    {
        return getMaxStackSize();
    }

    @Override
    public boolean mayPickup(Player player)
    {
        return !handler.extractItem(slotIndex, 1, true).isEmpty();
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return allowPlace && handler.isItemValid(slotIndex, stack);
    }

    @Override
    public ItemStack remove(int amount)
    {
        return handler.extractItem(slotIndex, amount, false);
    }
}