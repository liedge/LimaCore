package liedge.limacore.capability.itemhandler;

import liedge.limacore.blockentity.IOAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.items.ItemStackHandler;

public class LimaBlockEntityItemHandler extends ItemStackHandler implements LimaItemHandlerBase
{
    private final ItemHolderBlockEntity itemHolder;

    public LimaBlockEntityItemHandler(ItemHolderBlockEntity itemHolder, int size)
    {
        super(size);
        this.itemHolder = itemHolder;
    }

    public ItemHolderBlockEntity getParentObject()
    {
        return itemHolder;
    }

    @Override
    public IOAccess getSlotIOAccess(int slot)
    {
        return itemHolder.getExternalItemSlotIO(slot);
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
                if (isItemValid(i, toSet))
                {
                    setStackInSlot(i, toSet);
                }
            }
        }
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return itemHolder.isItemValid(slot, stack);
    }

    @Override
    public void onContentsChanged(int slot)
    {
        itemHolder.onItemSlotChanged(slot);
    }

    @Override
    protected void onLoad()
    {
        itemHolder.onItemHandlerLoaded();
    }
}