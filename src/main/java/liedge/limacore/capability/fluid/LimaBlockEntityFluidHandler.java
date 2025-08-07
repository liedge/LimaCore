package liedge.limacore.capability.fluid;

import com.google.common.base.Preconditions;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.util.LimaCollectionsUtil;
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
public class LimaBlockEntityFluidHandler implements LimaFluidHandler, INBTSerializable<ListTag>
{
    private final FluidHolderBlockEntity blockEntity;
    private final NonNullList<LimaFluidTank> tanks;

    public LimaBlockEntityFluidHandler(FluidHolderBlockEntity blockEntity, int size)
    {
        Preconditions.checkArgument(size > 0, "Fluid handler must have at least 1 tank.");
        this.blockEntity = blockEntity;
        this.tanks = NonNullList.createWithCapacity(size);

        for (int i = 0; i < size; i++)
        {
            tanks.add(new LimaFluidTank(blockEntity.getBaseFluidCapacity(i), blockEntity.getBaseFluidTransferRate(i)));
        }
    }

    public LimaBlockEntityFluidHandler(FluidHolderBlockEntity blockEntity)
    {
        this(blockEntity, 1);
    }

    public void syncAllTanks(DataWatcherHolder.DataWatcherCollector collector)
    {
        tanks.forEach(tank -> tank.syncTank(collector));
    }

    @Override
    public IOAccess getFluidTankIO(int tank)
    {
        return blockEntity.getFluidTankIO(tank);
    }

    @Override
    public LimaFluidTank getFluidTank(int tank) throws IndexOutOfBoundsException
    {
        validateTankIndex(tank);
        return tanks.get(tank);
    }

    @Override
    public int getTanks()
    {
        return tanks.size();
    }

    @Override
    public FluidStack getFluidInTank(int tank)
    {
        return getFluidTank(tank).getFluid();
    }

    @Override
    public int getTankCapacity(int tank)
    {
        return getFluidTank(tank).getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack)
    {
        return blockEntity.isValidFluid(tank, stack);
    }

    @Override
    public int fillTank(int tank, FluidStack resource, FluidAction action, boolean ignoreLimit)
    {
        int filled = getFluidTank(tank).fill(resource, action, ignoreLimit);
        if (filled == 0) return 0;
        if (!action.simulate()) blockEntity.onFluidsChanged(tank);

        return filled;
    }

    @Override
    public FluidStack drainTank(int tank, FluidStack resource, FluidAction action, boolean ignoreLimit)
    {
        FluidStack drained = getFluidTank(tank).drain(resource, action, ignoreLimit);
        if (drained.isEmpty()) return FluidStack.EMPTY;
        if (!action.simulate()) blockEntity.onFluidsChanged(tank);

        return drained;
    }

    @Override
    public FluidStack drainTank(int tank, int maxDrain, FluidAction action, boolean ignoreLimit)
    {
        FluidStack drained = getFluidTank(tank).drain(maxDrain, action, ignoreLimit);
        if (drained.isEmpty()) return FluidStack.EMPTY;
        if (!action.simulate()) blockEntity.onFluidsChanged(tank);

        return drained;
    }

    @Override
    public int fillAny(FluidStack resource, FluidAction action, boolean ignoreLimit)
    {
        for (int i = 0; i < tanks.size(); i++)
        {
            int filled = fillTank(i, resource, action, ignoreLimit);
            if (filled > 0) return filled;
        }

        return 0;
    }

    @Override
    public FluidStack drainFromAny(FluidStack resource, FluidAction action, boolean ignoreLimit)
    {
        for (int i = 0; i < tanks.size(); i++)
        {
            FluidStack drained = drainTank(i, resource, action, ignoreLimit);
            if (!drained.isEmpty()) return drained;
        }

        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drainFromAny(int maxDrain, FluidAction action, boolean ignoreLimit)
    {
        for (int i = 0; i < tanks.size(); i++)
        {
            FluidStack drained = drainTank(i, maxDrain, action, ignoreLimit);
            if (!drained.isEmpty()) return drained;
        }

        return FluidStack.EMPTY;
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

    private void validateTankIndex(int tank)
    {
        if (tank < 0 || tank >= tanks.size())
            throw new IndexOutOfBoundsException(String.format("Tank index %s out of valid range [0,%s)", tank, tanks.size()));
    }
}