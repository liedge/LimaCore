package liedge.limacore.inventory;

import liedge.limacore.lib.IODirection;
import net.minecraft.world.item.ItemStack;

public interface ItemHandlerHolder
{
    LimaItemStackHandler getItemHandler();

    void onItemSlotChanged(int slot);

    boolean isItemValid(int slot, ItemStack stack);

    default void itemHandlerLoaded() {}

    default IODirection getIOForSlot(int slot)
    {
        return IODirection.BOTH;
    }
}