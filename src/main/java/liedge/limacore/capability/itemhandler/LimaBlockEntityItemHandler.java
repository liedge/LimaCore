package liedge.limacore.capability.itemhandler;

import liedge.limacore.blockentity.IOAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.items.ItemStackHandler;

public class LimaBlockEntityItemHandler extends ItemStackHandler implements LimaItemHandlerBase
{
    private final ItemHolderBlockEntity blockEntity;
    private final int handlerIndex;

    public LimaBlockEntityItemHandler(ItemHolderBlockEntity blockEntity, int handlerIndex, int size)
    {
        super(size);
        this.blockEntity = blockEntity;
        this.handlerIndex = handlerIndex;
    }

    public LimaBlockEntityItemHandler(ItemHolderBlockEntity blockEntity, int size)
    {
        this(blockEntity, 0, size);
    }

    public ItemHolderBlockEntity getParentObject()
    {
        return blockEntity;
    }

    @Override
    public IOAccess getSlotIOAccess(int slot)
    {
        return blockEntity.getItemSlotIO(handlerIndex, slot);
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
        return blockEntity.isItemValid(handlerIndex, slot, stack);
    }

    @Override
    public void onContentsChanged(int slot)
    {
        blockEntity.onItemSlotChanged(handlerIndex, slot);
    }

    @Override
    protected void onLoad()
    {
        blockEntity.onItemHandlerLoaded(handlerIndex);
    }
}