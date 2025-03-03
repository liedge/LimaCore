package liedge.limacore.capability.itemhandler;

import liedge.limacore.blockentity.IOAccess;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public interface ItemHolderBlockEntity
{
    LimaItemHandlerBase getItemHandler();

    boolean isItemValid(int slot, ItemStack stack);

    void onItemSlotChanged(int slot);

    IOAccess getItemIOForSide(Direction side);

    default IOAccess getPerSlotIO(int slot)
    {
        return IOAccess.INPUT_AND_OUTPUT;
    }

    default void onItemHandlerLoaded() {}

    default @Nullable IItemHandler createItemIOWrapper(@Nullable Direction side)
    {
        if (side != null)
        {
            return new ItemHandlerIOWrapper(getItemHandler(), getItemIOForSide(side));
        }

        return null;
    }
}