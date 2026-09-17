package liedge.limacore.transfer.item;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntityAccess;
import liedge.limacore.transfer.LimaTransferUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.stream.Stream;

public interface ItemHolderBlockEntity extends LimaBlockEntityAccess
{
    @Nullable LimaBlockEntityItems getItems(BlockContentsType contentsType);

    default LimaBlockEntityItems getItemsOrThrow(BlockContentsType contentsType)
    {
        LimaBlockEntityItems handler = getItems(contentsType);
        if (handler == null) throw new IllegalArgumentException("Block entity does not support item contents type " + contentsType.getSerializedName());

        return handler;
    }

    default IOAccess getTopLevelItemIO(@Nullable Direction side)
    {
        return IOAccess.DISABLED;
    }

    default IOAccess getResourceLevelItemIO(BlockContentsType contentsType, int index, ItemResource resource)
    {
        return switch (contentsType)
        {
            case GENERAL -> IOAccess.INPUT_AND_OUTPUT;
            case AUXILIARY -> IOAccess.DISABLED;
            case INPUT -> IOAccess.INPUT_ONLY;
            case OUTPUT -> IOAccess.OUTPUT_ONLY;
        };
    }

    @ApiStatus.OverrideOnly
    default boolean isItemValid(BlockContentsType contentsType, int index, ItemResource resource)
    {
        return true;
    }

    @ApiStatus.OverrideOnly
    default void onItemChanged(BlockContentsType contentsType, int index, ItemStack previousContents)
    {
        setChanged();
    }

    default @Nullable ResourceHandler<ItemResource> createExternalItems(@Nullable Direction side)
    {
        if (side == null)
        {
            return itemsWrapper(IOAccess.DISABLED, BlockContentsType.INPUT, BlockContentsType.OUTPUT);
        }

        IOAccess topLevelAccess = getTopLevelItemIO(side);
        return switch (topLevelAccess)
        {
            case DISABLED -> null;
            case INPUT_ONLY -> itemsWrapper(topLevelAccess, BlockContentsType.INPUT);
            case OUTPUT_ONLY -> itemsWrapper(topLevelAccess, BlockContentsType.OUTPUT);
            case INPUT_AND_OUTPUT -> itemsWrapper(topLevelAccess, BlockContentsType.INPUT, BlockContentsType.OUTPUT);
        };
    }

    default void loadItemResources(ValueInput global)
    {
        LimaTransferUtil.loadBlockResources(global, LimaCommonConstants.KEY_ITEM_CONTAINER, this::getItems);
    }

    default void saveItemResources(ValueOutput global)
    {
        LimaTransferUtil.saveBlockResources(global, LimaCommonConstants.KEY_ITEM_CONTAINER, this::getItems);
    }

    private @Nullable ResourceHandler<ItemResource> itemsWrapper(IOAccess topLevelAccess, BlockContentsType type)
    {
        LimaBlockEntityItems items = getItems(type);
        return items != null ? items.createIOWrapper(topLevelAccess) : null;
    }

    private @Nullable ResourceHandler<ItemResource> itemsWrapper(IOAccess topLevelAccess, BlockContentsType... types)
    {
        return LimaTransferUtil.mergeNullableHandlers(Stream.of(types).map(t -> itemsWrapper(topLevelAccess, t)));
    }
}