package liedge.limacore.capability.itemhandler;

import liedge.limacore.capability.IOAccess;
import net.minecraft.world.item.ItemStack;

public interface ItemHandlerHolder
{
    LimaItemHandlerBase getItemHandler();

    void onItemSlotChanged(int slot);

    boolean isItemValid(int slot, ItemStack stack);

    default void itemHandlerLoaded() {}

    default IOAccess getSlotIOAccess(int slot)
    {
        return IOAccess.INPUT_AND_OUTPUT;
    }
}