package liedge.limacore.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.items.ItemStackHandler;

public class LimaItemStackHandler extends ItemStackHandler
{
    private final ItemHandlerHolder holder;

    public LimaItemStackHandler(ItemHandlerHolder holder, int size)
    {
        super(size);
        this.holder = holder;
    }

    public ItemHandlerHolder getParentObject()
    {
        return holder;
    }

    public ItemContainerContents copyToComponent()
    {
        return ItemContainerContents.fromItems(this.stacks);
    }

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
        return holder.isItemValid(slot, stack);
    }

    @Override
    public void onContentsChanged(int slot)
    {
        holder.onItemSlotChanged(slot);
    }

    @Override
    protected void onLoad()
    {
        holder.itemHandlerLoaded();
    }
}