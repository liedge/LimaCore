package liedge.limacore.inventory.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LockedHotbarSlot extends Slot
{
    public LockedHotbarSlot(Container container, int slotIndex, int x, int y)
    {
        super(container, slotIndex, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean mayPickup(Player player)
    {
        return false;
    }

    @Override
    public ItemStack remove(int amount)
    {
        return ItemStack.EMPTY;
    }
}