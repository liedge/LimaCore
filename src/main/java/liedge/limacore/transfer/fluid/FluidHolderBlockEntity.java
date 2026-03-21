package liedge.limacore.transfer.fluid;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntityAccess;
import liedge.limacore.transfer.LimaTransferUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

public interface FluidHolderBlockEntity extends LimaBlockEntityAccess
{
    @Nullable LimaBlockEntityFluids getFluids(BlockContentsType contentsType);

    default LimaBlockEntityFluids getFluidsOrThrow(BlockContentsType contentsType)
    {
        LimaBlockEntityFluids handler = getFluids(contentsType);
        if (handler == null) throw new IllegalArgumentException("Block entity does not support fluid contents type " + contentsType.getSerializedName());

        return handler;
    }

    int getBaseFluidCapacity(BlockContentsType contentsType);

    int getBaseFluidTransferRate(BlockContentsType contentsType);

    default IOAccess getTopLevelFluidIO(@Nullable Direction side)
    {
        return IOAccess.DISABLED;
    }

    default IOAccess getResourceLevelFluidIO(BlockContentsType contentsType, int index, FluidResource resource)
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
    default boolean isFluidValid(BlockContentsType contentsType, int index, FluidResource resource)
    {
        return true;
    }

    @ApiStatus.OverrideOnly
    default void onFluidChanged(BlockContentsType contentsType, int index, FluidStack previousContents)
    {
        setChanged();
    }

    default @Nullable ResourceHandler<FluidResource> createExternalFluids(@Nullable Direction side)
    {
        IOAccess topLevelAccess = getTopLevelFluidIO(side);
        return switch (topLevelAccess)
        {
            case DISABLED -> null;
            case INPUT_ONLY -> fluidsWrapper(BlockContentsType.INPUT, topLevelAccess);
            case OUTPUT_ONLY -> fluidsWrapper(BlockContentsType.OUTPUT, topLevelAccess);
            case INPUT_AND_OUTPUT -> LimaTransferUtil.mergeInputOutputHandlers(fluidsWrapper(BlockContentsType.INPUT, topLevelAccess), fluidsWrapper(BlockContentsType.OUTPUT, topLevelAccess));
        };
    }

    default void loadFluidResources(ValueInput global)
    {
        LimaTransferUtil.loadBlockResources(global, LimaCommonConstants.KEY_FLUIDS_CONTAINER, this::getFluids);
    }

    default void saveFluidResources(ValueOutput global)
    {
        LimaTransferUtil.saveBlockResources(global, LimaCommonConstants.KEY_FLUIDS_CONTAINER, this::getFluids);
    }

    private @Nullable ResourceHandler<FluidResource> fluidsWrapper(BlockContentsType contentsType, IOAccess topLevelAccess)
    {
        LimaBlockEntityFluids fluids = getFluids(contentsType);
        return fluids != null ? fluids.createIOWrapper(topLevelAccess) : null;
    }
}