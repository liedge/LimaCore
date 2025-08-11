package liedge.limacore.capability.fluid;

import com.google.common.base.Preconditions;
import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.limacore.util.LimaStreamsUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Implementation of {@link LimaFluidHandler} specialized for use in fluid-handling block entities.
 * It is not recommended to expose instances of this class directly for capability access unless
 * it contains 1 tank and IO control is not required. Otherwise, create a capability wrapper instance
 * with {@link FluidHolderBlockEntity#createFluidIOWrapper(Direction)}.
 */
public class LimaBlockEntityFluidHandler extends LimaFluidHandler implements INBTSerializable<ListTag>
{
    private final NonNullList<VariableFluidTank> tanks;
    private final FluidHolderBlockEntity blockEntity;
    private final BlockContentsType contentsType;

    public LimaBlockEntityFluidHandler(FluidHolderBlockEntity blockEntity, int size, BlockContentsType contentsType)
    {
        Preconditions.checkArgument(size > 0, "Fluid handler must have at least 1 tank.");
        this.tanks = NonNullList.createWithCapacity(size);
        this.blockEntity = blockEntity;
        this.contentsType = contentsType;

        for (int i = 0; i < size; i++)
        {
            VariableFluidTank tank = new VariableFluidTank(blockEntity.getBaseFluidCapacity(contentsType, i), blockEntity.getBaseFluidTransferRate(contentsType, i));
            tanks.add(tank);
        }
    }

    public void syncAllTanks(DataWatcherHolder.DataWatcherCollector collector)
    {
        tanks.forEach(tank -> tank.syncTank(collector));
    }

    public FluidHandlerIOWrapper createIOWrapper(IOAccess blockAccessLevel)
    {
        return new IOWrapper(this, blockAccessLevel);
    }

    public LimaFluidHandler copyHandler()
    {
        NonNullList<LimaFluidTank> copiedTanks = tanks.stream().map(VariableFluidTank::copy).collect(LimaStreamsUtil.toNonNullList());
        return new SimpleFluidHandler(copiedTanks);
    }

    @Override
    protected NonNullList<VariableFluidTank> getFluidTanks()
    {
        return tanks;
    }

    @Override
    protected void onTankChanged(int tank)
    {
        blockEntity.onFluidsChanged(contentsType, tank);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack)
    {
        return blockEntity.isValidFluid(contentsType, tank, stack);
    }

    @Override
    public ListTag serializeNBT(HolderLookup.Provider provider)
    {
        ListTag tag = new ListTag();

        for (int i = 0; i < tanks.size(); i++)
        {
            LimaFluidTank tank = tanks.get(i);
            if (!tank.getFluid().isEmpty())
            {
                CompoundTag tankTag = new CompoundTag();
                tankTag.putInt("tank", i);
                tag.add(tank.getFluid().save(provider, tankTag));
            }
        }

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, ListTag nbt)
    {
        LimaCollectionsUtil.streamCompoundList(nbt).forEach(tankTag ->
        {
            int tank = tankTag.getInt("tank");
            FluidStack stack = FluidStack.parseOptional(provider, tankTag);
            getFluidTank(tank).setFluid(stack);
        });
    }

    private record IOWrapper(LimaBlockEntityFluidHandler fluidHandler, IOAccess blockAccessLevel) implements FluidHandlerIOWrapper
    {
        @Override
        public int fill(FluidStack resource, FluidAction action)
        {
            if (blockAccessLevel.allowsInput())
            {
                for (int i = 0; i < getTanks(); i++)
                {
                    if (fluidHandler.blockEntity.getFluidTankIO(fluidHandler.contentsType, i).allowsInput())
                    {
                        int filled = fluidHandler.fillTank(i, resource, action, false);
                        if (filled > 0) return filled;
                    }
                }
            }

            return 0;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action)
        {
            if (blockAccessLevel.allowsOutput())
            {
                for (int i = 0; i < getTanks(); i++)
                {
                    if (fluidHandler.blockEntity.getFluidTankIO(fluidHandler.contentsType, i).allowsOutput())
                    {
                        FluidStack drained = fluidHandler.drainTank(i, resource, action, false);
                        if (!drained.isEmpty()) return drained;
                    }
                }
            }

            return FluidStack.EMPTY;
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action)
        {
            if (blockAccessLevel.allowsOutput())
            {
                for (int i = 0; i < getTanks(); i++)
                {
                    if (fluidHandler.blockEntity.getFluidTankIO(fluidHandler.contentsType, i).allowsOutput())
                    {
                        FluidStack drained = fluidHandler.drainTank(i, maxDrain, action, false);
                        if (!drained.isEmpty()) return drained;
                    }
                }
            }

            return FluidStack.EMPTY;
        }
    }
}