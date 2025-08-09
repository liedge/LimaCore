package liedge.limacore.capability.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/**
 * An extension of {@link IFluidHandler} meant for use with {@link LimaFluidTank} instances. Provides extended
 * fluid operations with finer control for transfer limits and IO direction limitations.
 */
public interface LimaFluidHandler extends IFluidHandler
{
    LimaFluidTank getFluidTank(int tank) throws IndexOutOfBoundsException;

    int fillTank(int tank, FluidStack resource, FluidAction action, boolean ignoreLimit);

    FluidStack drainTank(int tank, FluidStack resource, FluidAction action, boolean ignoreLimit);

    FluidStack drainTank(int tank, int maxDrain, FluidAction action, boolean ignoreLimit);

    int fillAny(FluidStack resource, FluidAction action, boolean ignoreLimit);

    FluidStack drainFromAny(FluidStack resource, FluidAction action, boolean ignoreLimit);

    int fillFirst(FluidStack resource, FluidAction action, boolean ignoreLimit);

    FluidStack drainFromFirst(FluidStack resource, FluidAction action, boolean ignoreLimit);

    FluidStack drainFromFirst(int maxDrain, FluidAction action, boolean ignoreLimit);

    /**
     * @deprecated For capability support only.
     */
    @Deprecated
    @Override
    default int fill(FluidStack resource, FluidAction action)
    {
        return fillFirst(resource, action, false);
    }

    /**
     * @deprecated For capability support only.
     */
    @Deprecated
    @Override
    default FluidStack drain(FluidStack resource, FluidAction action)
    {
        return drainFromFirst(resource, action, false);
    }

    /**
     * @deprecated For capability support only.
     */
    @Deprecated
    @Override
    default FluidStack drain(int maxDrain, FluidAction action)
    {
        return drainFromFirst(maxDrain, action, false);
    }
}