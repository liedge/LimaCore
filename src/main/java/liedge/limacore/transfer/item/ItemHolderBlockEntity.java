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

public interface ItemHolderBlockEntity extends LimaBlockEntityAccess
{
    @Nullable LimaBlockEntityItems getItems(BlockContentsType contentsType);

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
        IOAccess topLevelAccess = getTopLevelItemIO(side);
        return switch (topLevelAccess)
        {
            case DISABLED -> null;
            case INPUT_ONLY -> itemsWrapper(BlockContentsType.INPUT, topLevelAccess);
            case OUTPUT_ONLY -> itemsWrapper(BlockContentsType.OUTPUT, topLevelAccess);
            case INPUT_AND_OUTPUT -> LimaTransferUtil.mergeInputOutputHandlers(itemsWrapper(BlockContentsType.INPUT, topLevelAccess), itemsWrapper(BlockContentsType.OUTPUT, topLevelAccess));
        };
    }

    default void loadItemResources(ValueInput global)
    {
        ValueInput input = global.child(LimaCommonConstants.KEY_ITEM_CONTAINER).orElse(null);
        if (input == null) return;

        for (BlockContentsType type : BlockContentsType.values())
        {
            LimaBlockEntityItems items = getItems(type);
            if (items != null)
            {
                input.child(type.getSerializedName()).ifPresent(items::deserialize);
            }
        }
    }

    default void saveItemResources(ValueOutput global)
    {
        ValueOutput output = global.child(LimaCommonConstants.KEY_ITEM_CONTAINER);

        for (BlockContentsType type : BlockContentsType.values())
        {
            LimaBlockEntityItems items = getItems(type);
            if (items != null)
            {
                items.serialize(output.child(type.getSerializedName()));
            }
        }
    }

    private @Nullable ResourceHandler<ItemResource> itemsWrapper(BlockContentsType contentsType, IOAccess topLevelAccess)
    {
        LimaBlockEntityItems items = getItems(contentsType);
        return items != null ? items.createIOWrapper(topLevelAccess) : null;
    }
}