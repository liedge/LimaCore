package liedge.limacore.capability.itemhandler;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntityAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.items.ItemStackHandler;

public class StandaloneBlockEntityItemHandler extends ItemStackHandler implements LimaItemHandlerBase
{
    private final LimaBlockEntityAccess parent;

    public StandaloneBlockEntityItemHandler(LimaBlockEntityAccess parent, int size)
    {
        super(size);
        this.parent = parent;
    }

    @Override
    public IOAccess getSlotIOAccess(int slot)
    {
        return IOAccess.INPUT_AND_OUTPUT;
    }

    @Override
    public ItemContainerContents copyToComponent()
    {
        return ItemContainerContents.fromItems(this.stacks);
    }

    @Override
    public void copyFromComponent(ItemContainerContents contents)
    {
        for (int i = 0; i < contents.getSlots(); i++)
        {
            if (i < stacks.size())
            {
                ItemStack toSet = contents.getStackInSlot(i);
                if (isItemValid(i, toSet)) setStackInSlot(i, toSet);
            }
        }
    }

    @Override
    public void onContentsChanged(int index)
    {
        parent.setChanged();
    }
}