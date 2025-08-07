package liedge.limacore.capability.fluid;

import liedge.limacore.blockentity.IOAccess;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/**
 * An extension of {@link IFluidHandler} meant for use with {@link LimaFluidTank} instances. Provides extended
 * fluid operations with finer control for transfer limits and IO direction limitations.
 */
public interface LimaFluidHandler extends IFluidHandler
{
    LimaFluidTank getFluidTank(int tank) throws IndexOutOfBoundsException;

    IOAccess getFluidTankIO(int tank);

    int fillTank(int tank, FluidStack resource, FluidAction action, boolean ignoreLimit);

    FluidStack drainTank(int tank, FluidStack resource, FluidAction action, boolean ignoreLimit);

    FluidStack drainTank(int tank, int maxDrain, FluidAction action, boolean ignoreLimit);

    int fillAny(FluidStack resource, FluidAction action, boolean ignoreLimit);

    FluidStack drainFromAny(FluidStack resource, FluidAction action, boolean ignoreLimit);

    FluidStack drainFromAny(int maxDrain, FluidAction action, boolean ignoreLimit);

    /**
     * @deprecated For capability support only. Call {@link LimaFluidHandler#drainFromAny(int, FluidAction, boolean)} instead.
     */
    @Deprecated
    @Override
    default int fill(FluidStack resource, FluidAction action)
    {
        return fillAny(resource, action, false);
    }

    /**
     * @deprecated For capability support only. Call {@link LimaFluidHandler#drainFromAny(FluidStack, FluidAction, boolean)} instead.
     */
    @Deprecated
    @Override
    default FluidStack drain(FluidStack resource, FluidAction action)
    {
        return drainFromAny(resource, action, false);
    }

    /**
     * @deprecated For capability support only. Call {@link LimaFluidHandler#drainFromAny(int, FluidAction, boolean)} instead.
     */
    @Deprecated
    @Override
    default FluidStack drain(int maxDrain, FluidAction action)
    {
        return drainFromAny(maxDrain, action, false);
    }
}