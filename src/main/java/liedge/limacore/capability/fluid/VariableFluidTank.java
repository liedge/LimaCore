package liedge.limacore.capability.fluid;

import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.ApiStatus;

public class VariableFluidTank implements LimaFluidTank
{
    private FluidStack fluid;
    private int capacity;
    private int transferRate;

    public VariableFluidTank(FluidStack fluid, int capacity, int transferRate)
    {
        this.fluid = fluid;
        this.capacity = capacity;
        this.transferRate = transferRate;
    }

    public VariableFluidTank(int capacity, int transferRate)
    {
        this(FluidStack.EMPTY, capacity, transferRate);
    }

    public LimaDataWatcher<FluidStack> syncFluid()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.FLUID_STACK, () -> fluid.copy(), this::setFluid);
    }

    public LimaDataWatcher<Integer> syncCapacity()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getCapacity, this::setCapacity);
    }

    public LimaDataWatcher<Integer> syncTransferRate()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getTransferRate, this::setTransferRate);
    }

    public void syncTank(DataWatcherHolder.DataWatcherCollector collector)
    {
        collector.register(syncFluid());
        collector.register(syncCapacity());
        collector.register(syncTransferRate());
    }

    @Override
    public FluidStack getFluid()
    {
        return fluid;
    }

    @Override
    public LimaFluidTank copy()
    {
        return new VariableFluidTank(fluid.copy(), capacity, transferRate);
    }

    @ApiStatus.Internal
    @Override
    public void setFluid(FluidStack fluid)
    {
        this.fluid = fluid;
    }

    @Override
    public int getFluidAmount()
    {
        return fluid.getAmount();
    }

    @Override
    public int getCapacity()
    {
        return capacity;
    }

    public void setCapacity(int capacity)
    {
        this.capacity = capacity;
    }

    public int getTransferRate()
    {
        return transferRate;
    }

    public void setTransferRate(int transferRate)
    {
        this.transferRate = transferRate;
    }

    @Override
    public boolean isFluidValid(FluidStack stack)
    {
        return true;
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action, boolean ignoreLimit)
    {
        // Return early if fluid to be inserted is empty, or we have a non-empty tank and mismatching fluids
        if (resource.isEmpty() || (!FluidStack.isSameFluidSameComponents(this.fluid, resource) && !this.fluid.isEmpty())) return 0;

        final int limit = ignoreLimit ? Integer.MAX_VALUE : transferRate;
        final int stored = this.fluid.getAmount();

        int actuallyReceived = Math.min(capacity - stored, Math.min(resource.getAmount(), limit));

        if (action.simulate()) return actuallyReceived;

        if (this.fluid.isEmpty())
        {
            this.fluid = resource.copyWithAmount(actuallyReceived);
        }
        else
        {
            this.fluid.grow(actuallyReceived);
        }

        return actuallyReceived;
    }

    @Override
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action, boolean ignoreLimit)
    {
        if (maxDrain <= 0 || this.fluid.isEmpty()) return FluidStack.EMPTY;

        final int limit = ignoreLimit ? Integer.MAX_VALUE : transferRate;
        final int stored = this.fluid.getAmount();

        int actuallyDrained = Math.min(stored, Math.min(maxDrain, limit));
        FluidStack result = this.fluid.copyWithAmount(actuallyDrained);

        if (!action.simulate() && actuallyDrained > 0) this.fluid.shrink(actuallyDrained);

        return result;
    }

    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action, boolean ignoreLimit)
    {
        return !resource.isEmpty() && FluidStack.isSameFluidSameComponents(this.fluid, resource)
                ? drain(resource.getAmount(), action, ignoreLimit) : FluidStack.EMPTY;
    }
}