package liedge.limacore.transfer.item;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.transfer.ExternalResourceHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class LimaBlockEntityItems extends ItemStacksResourceHandler
{
    private final ItemHolderBlockEntity blockEntity;
    private final BlockContentsType contentsType;
    private final int minSize;

    public LimaBlockEntityItems(ItemHolderBlockEntity blockEntity, BlockContentsType contentsType, int size)
    {
        super(size);
        this.blockEntity = blockEntity;
        this.contentsType = contentsType;
        this.minSize = size;
    }

    public ResourceHandler<ItemResource> createIOWrapper(IOAccess topLevelAccess)
    {
        return new ExternalWrapper(this, topLevelAccess);
    }

    @Override
    protected int getCapacity(int index, ItemResource resource)
    {
        return resource.isEmpty() ? Item.DEFAULT_MAX_STACK_SIZE : Math.min(resource.getMaxStackSize(), Item.DEFAULT_MAX_STACK_SIZE);
    }

    @Override
    public boolean isValid(int index, ItemResource resource)
    {
        return blockEntity.isItemValid(contentsType, index, resource);
    }

    @Override
    protected void onContentsChanged(int index, ItemStack previousContents)
    {
        blockEntity.onItemChanged(contentsType, index, previousContents);
    }

    @Override
    public void deserialize(ValueInput input)
    {
        input.read(VALUE_IO_KEY, codec).ifPresent(list ->
        {
            int size = Math.max(list.size(), this.minSize);
            NonNullList<ItemStack> fixedStacks = NonNullList.withSize(size, ItemStack.EMPTY);

            for (int i = 0; i < list.size(); i++)
            {
                fixedStacks.set(i, list.get(i));
            }

            setStacks(fixedStacks);
        });
    }

    private static class ExternalWrapper extends ExternalResourceHandler<ItemResource, LimaBlockEntityItems>
    {
        ExternalWrapper(LimaBlockEntityItems base, IOAccess topLevelAccess)
        {
            super(base, topLevelAccess);
        }

        @Override
        protected boolean canInsert(LimaBlockEntityItems base, int index, ItemResource resource, IOAccess topLevelAccess)
        {
            return topLevelAccess.allowsInput() && base.blockEntity.getResourceLevelItemIO(base.contentsType, index, resource).allowsInput();
        }

        @Override
        protected boolean canExtract(LimaBlockEntityItems base, int index, ItemResource resource, IOAccess topLevelAccess)
        {
            return topLevelAccess.allowsOutput() && base.blockEntity.getResourceLevelItemIO(base.contentsType, index, resource).allowsOutput();
        }

        @Override
        protected int getTransferLimit(LimaBlockEntityItems base)
        {
            return Integer.MAX_VALUE;
        }
    }
}