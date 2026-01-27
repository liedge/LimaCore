package liedge.limacore.capability.fluid;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntityAccess;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public interface FluidHolderBlockEntity extends LimaBlockEntityAccess
{
    @Nullable LimaBlockEntityFluidHandler getFluidHandler(BlockContentsType contentsType);

    default LimaBlockEntityFluidHandler getFluidHandlerOrThrow(BlockContentsType contentsType)
    {
        LimaBlockEntityFluidHandler handler = getFluidHandler(contentsType);
        if (handler != null)
            return handler;
        else
            throw new IllegalArgumentException("Block entity does not support fluid contents type " + contentsType.getSerializedName());
    }

    int getBaseFluidCapacity(BlockContentsType contentsType, int tank);

    int getBaseFluidTransferRate(BlockContentsType contentsType, int tank);

    boolean isValidFluid(BlockContentsType contentsType, int tank, FluidStack stack);

    IOAccess getSideIOForFluids(@Nullable Direction side);

    @ApiStatus.OverrideOnly
    default IOAccess getFluidTankIO(BlockContentsType contentsType, int tank)
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
    default void onFluidsChanged(BlockContentsType contentsType, int tank)
    {
        setChanged();
    }

    /**
     * Creates, if compatible, a {@link FluidHandlerIOWrapper} for the given side. Preferably, centralize the creation of
     * capability objects for {@link RegisterCapabilitiesEvent} using this method.
     * @param side The side to create a capability wrapper for.
     * @return The fluid handler wrapper, or {@code null} if not compatible.
     */
    @Nullable IFluidHandler createFluidIOWrapper(@Nullable Direction side);

    @ApiStatus.NonExtendable
    default @Nullable IFluidHandler wrapInputOutputTanks(@Nullable Direction side)
    {
        IOAccess blockAccessLevel = getSideIOForFluids(side);
        return switch (blockAccessLevel)
        {
            case DISABLED -> null;
            case INPUT_ONLY -> fluidWrapper(BlockContentsType.INPUT, blockAccessLevel);
            case OUTPUT_ONLY -> fluidWrapper(BlockContentsType.OUTPUT, blockAccessLevel);
            case INPUT_AND_OUTPUT -> new CombinedFluidsWrapper(fluidWrapper(BlockContentsType.INPUT, blockAccessLevel), fluidWrapper(BlockContentsType.OUTPUT, blockAccessLevel));
        };
    }

    default void loadFluidContainers(CompoundTag tag, HolderLookup.Provider registries)
    {
        CompoundTag containersTag = tag.getCompound(LimaCommonConstants.KEY_FLUID_TANKS);
        if (containersTag.isEmpty()) return;

        for (BlockContentsType type : BlockContentsType.values())
        {
            LimaBlockEntityFluidHandler handler = getFluidHandler(type);
            if (handler != null && containersTag.contains(type.getSerializedName(), Tag.TAG_LIST))
            {
                ListTag handlerTag = containersTag.getList(type.getSerializedName(), Tag.TAG_COMPOUND);
                handler.deserializeNBT(registries, handlerTag);
            }
        }
    }

    default void saveFluidContainers(CompoundTag tag, HolderLookup.Provider registries)
    {
        CompoundTag containersTag = new CompoundTag();

        for (BlockContentsType type : BlockContentsType.values())
        {
            LimaBlockEntityFluidHandler handler = getFluidHandler(type);
            if (handler != null)
            {
                ListTag handlerTag = handler.serializeNBT(registries);
                containersTag.put(type.getSerializedName(), handlerTag);
            }
        }

        if (!containersTag.isEmpty()) tag.put(LimaCommonConstants.KEY_FLUID_TANKS, containersTag);
    }

    private FluidHandlerIOWrapper fluidWrapper(BlockContentsType contentsType, IOAccess blockAccessLevel)
    {
        return getFluidHandlerOrThrow(contentsType).createIOWrapper(blockAccessLevel);
    }
}