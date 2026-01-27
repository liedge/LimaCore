package liedge.limacore.capability.itemhandler;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntityAccess;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public interface ItemHolderBlockEntity extends LimaBlockEntityAccess
{
    /**
     * Tries to retrieve the {@link LimaBlockEntityItemHandler} associated with the given {@link BlockContentsType}.
     * @param contentsType The contents type of the item handler.
     * @return The item handler for the given type, or {@code null} if none exists.
     * @apiNote Unless absolutely necessary, use {@link ItemHolderBlockEntity#getItemHandlerOrThrow(BlockContentsType)} for
     * proper access enforcement.
     */
    @Nullable LimaBlockEntityItemHandler getItemHandler(BlockContentsType contentsType);

    /**
     * Retrieves the {@link LimaBlockEntityItemHandler} associated with the given {@link BlockContentsType}, or throws
     * an exception if none exists.
     * @param contentsType The contents type of item handler.
     * @return The item handler for the given type
     * @throws IllegalArgumentException If the block entity does not support the given type.
     */
    default LimaBlockEntityItemHandler getItemHandlerOrThrow(BlockContentsType contentsType)
    {
        LimaBlockEntityItemHandler handler = getItemHandler(contentsType);
        if (handler != null)
            return handler;
        else
            throw new IllegalArgumentException("Block entity does not support item contents type " + contentsType.getSerializedName());
    }

    /**
     * Checks if an {@link ItemStack} is valid for a specific slot in the specified item handler.
     * Called by {@link LimaBlockEntityItemHandler#isItemValid(int, ItemStack)} using its handler index.
     * @param contentsType The contents type of the item handler calling this function.
     * @param slot The item handler slot to be checked
     * @param stack The item stack to be checked
     * @return If the item is valid.
     */
    @ApiStatus.OverrideOnly
    boolean isItemValid(BlockContentsType contentsType, int slot, ItemStack stack);

    /**
     * Called by {@link LimaBlockEntityItemHandler#onContentsChanged(int)} whenever a slot's contents are modified.
     * Default implementation marks the block entity as changed.
     * @param contentsType The contents type of the handler that changed.
     * @param slot The slot of the item handler that changed.
     */
    @ApiStatus.OverrideOnly
    default void onItemSlotChanged(BlockContentsType contentsType, int slot)
    {
        setChanged();
    }

    /**
     * Declares the {@link IOAccess} permission for the specified side. This is the top level permission and applies
     * to all of this block entity's item handlers.
     * @param side The side to check.
     * @return The IO access permission for the block entity's side.
     */
    IOAccess getSideIOForItems(@Nullable Direction side);

    /**
     * Gets the {@link IOAccess} permission at the slot level for an individual item handler. The default implementation
     * provides all-purpose defaults. This method is only used for wrappers created by
     * {@link LimaBlockEntityItemHandler#createIOWrapper(IOAccess)}.
     * @param contentsType The contents type of the item handler.
     * @param slot The slot index.
     * @return The slot's IO permission level.
     */
    @ApiStatus.OverrideOnly
    default IOAccess getItemHandlerSlotIO(BlockContentsType contentsType, int slot)
    {
        return switch (contentsType)
        {
            case GENERAL -> IOAccess.INPUT_AND_OUTPUT;
            case AUXILIARY -> IOAccess.DISABLED;
            case INPUT -> IOAccess.INPUT_ONLY;
            case OUTPUT -> IOAccess.OUTPUT_ONLY;
        };
    }

    /**
     * Called by {@link LimaBlockEntityItemHandler#onLoad()} whenever it is loaded, using its handler index.
     * @param contentsType The item handler's contents type.
     */
    @ApiStatus.OverrideOnly
    default void onItemHandlerLoaded(BlockContentsType contentsType) {}

    // Capability helper objects

    /**
     * Creates, if compatible, a {@link ItemHandlerIOWrapper} for the given side. Preferably, centralize the creation of
     * capability objects for {@link RegisterCapabilitiesEvent} using this method.
     * @param side The side to create a capability wrapper for.
     * @return The item handler wrapper, or {@code null} if not compatible.
     */
    @Nullable IItemHandler createItemIOWrapper(@Nullable Direction side);

    @ApiStatus.NonExtendable
    default @Nullable IItemHandler wrapInputOutputInventories(@Nullable Direction side)
    {
        IOAccess blockAccessLevel = getSideIOForItems(side);
        return switch (blockAccessLevel)
        {
            case DISABLED -> null;
            case INPUT_ONLY -> itemWrapper(BlockContentsType.INPUT, blockAccessLevel);
            case OUTPUT_ONLY -> itemWrapper(BlockContentsType.OUTPUT, blockAccessLevel);
            case INPUT_AND_OUTPUT -> new CombinedInvWrapper(itemWrapper(BlockContentsType.INPUT, blockAccessLevel), itemWrapper(BlockContentsType.OUTPUT, blockAccessLevel));
        };
    }

    default void loadItemContainers(CompoundTag tag, HolderLookup.Provider registries)
    {
        CompoundTag containersTag = tag.getCompound(LimaCommonConstants.KEY_ITEM_CONTAINER);
        if (containersTag.isEmpty()) return;

        for (BlockContentsType type : BlockContentsType.values())
        {
            LimaBlockEntityItemHandler handler = getItemHandler(type);
            if (handler != null && containersTag.contains(type.getSerializedName(), Tag.TAG_COMPOUND))
            {
                CompoundTag handlerTag = containersTag.getCompound(type.getSerializedName());
                handler.deserializeNBT(registries, handlerTag);
            }
        }
    }

    default void saveItemContainers(CompoundTag tag, HolderLookup.Provider registries)
    {
        CompoundTag containersTag = new CompoundTag();

        for (BlockContentsType type : BlockContentsType.values())
        {
            LimaBlockEntityItemHandler handler = getItemHandler(type);
            if (handler != null)
            {
                CompoundTag handlerTag = handler.serializeNBT(registries);
                containersTag.put(type.getSerializedName(), handlerTag);
            }
        }

        if (!containersTag.isEmpty()) tag.put(LimaCommonConstants.KEY_ITEM_CONTAINER, containersTag);
    }

    private ItemHandlerIOWrapper itemWrapper(BlockContentsType contentsType, IOAccess blockAccessLevel)
    {
        return getItemHandlerOrThrow(contentsType).createIOWrapper(blockAccessLevel);
    }
}