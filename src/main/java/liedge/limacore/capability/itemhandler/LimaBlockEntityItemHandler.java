package liedge.limacore.capability.itemhandler;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.util.LimaNbtUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

public class LimaBlockEntityItemHandler extends ItemStackHandler implements LimaItemHandler
{
    private final ItemHolderBlockEntity blockEntity;
    private final BlockInventoryType inventoryType;

    public LimaBlockEntityItemHandler(ItemHolderBlockEntity blockEntity, int size, BlockInventoryType inventoryType)
    {
        super(size);
        this.blockEntity = blockEntity;
        this.inventoryType = inventoryType;
    }

    public ItemHandlerIOWrapper createIOWrapper(IOAccess blockAccessLevel)
    {
        return new IOWrapper(this, blockAccessLevel);
    }

    /**
     * @deprecated Use {@link LimaBlockEntityItemHandler#resizeInventory(int)} to avoid loss of contents.
     */
    @Deprecated
    @Override
    public void setSize(int size)
    {
        super.setSize(size);
    }

    /**
     * Resizes the inventory list backing this Item Handler. Will capture stray item stacks that would be lost
     * when shrinking inventory size.
     * @param newSize The new size of the inventory.
     * @return List of remaining item stacks when {@code newSize < getSlots()} or an empty list if {@code newSize >= getSlots()}.
     * @apiNote The {@code blockEntity} parent must be marked as changed. Any open menus and capabilities should be invalidated.
     */
    public List<ItemStack> resizeInventory(int newSize)
    {
        Preconditions.checkArgument(newSize > 0, "Inventory size must be greater than 0.");
        int currentSize = stacks.size();

        if (newSize == currentSize)
        {
            return List.of();
        }
        else if (newSize > currentSize)
        {
            NonNullList<ItemStack> newStacks = NonNullList.withSize(newSize, ItemStack.EMPTY);

            for (int i = 0; i < currentSize; i++)
            {
                newStacks.set(i, stacks.get(i));
            }

            this.stacks = newStacks;

            return List.of();
        }
        else
        {
            NonNullList<ItemStack> newStacks = NonNullList.withSize(newSize, ItemStack.EMPTY);
            List<ItemStack> remainingStacks = new ObjectArrayList<>();

            for (int i = 0; i < currentSize; i++)
            {
                ItemStack current = stacks.get(i);

                if (i < newSize)
                    newStacks.set(i, current);
                else if (!current.isEmpty())
                    remainingStacks.add(current.copy());
            }

            this.stacks = newStacks;

            return remainingStacks;
        }
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
        return blockEntity.isItemValid(inventoryType, slot, stack);
    }

    @Override
    public void onContentsChanged(int slot)
    {
        blockEntity.onItemSlotChanged(inventoryType, slot);
    }

    @Override
    protected void onLoad()
    {
        blockEntity.onItemHandlerLoaded(inventoryType);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt)
    {
        // We use the larger value here to allow for retroactive expansion from version changes.
        int size = Math.max(stacks.size(), LimaNbtUtil.getAsInt(nbt, "Size", -1));
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);

        ListTag listTag = nbt.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++)
        {
            CompoundTag stackTag = listTag.getCompound(i);
            int slot = stackTag.getInt("Slot");

            if (slot >= 0 && slot < stacks.size())
            {
                ItemStack.parse(provider, stackTag).ifPresent(stack -> stacks.set(slot, stack));
            }
        }

        onLoad();
    }

    private record IOWrapper(LimaBlockEntityItemHandler source, IOAccess blockAccessLevel) implements ItemHandlerIOWrapper
    {
        @Override
        public boolean allowInput(int slot)
        {
            return blockAccessLevel.allowsInput() && source.blockEntity.getItemHandlerSlotIO(source.inventoryType, slot).allowsInput();
        }

        @Override
        public boolean allowOutput(int slot)
        {
            return blockAccessLevel.allowsOutput() && source.blockEntity.getItemHandlerSlotIO(source.inventoryType, slot).allowsOutput();
        }
    }
}