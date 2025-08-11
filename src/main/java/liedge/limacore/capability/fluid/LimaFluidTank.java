package liedge.limacore.capability.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.ApiStatus;

public interface LimaFluidTank extends IFluidTank
{
    /**
     * Copies the contents of the fluid tank. Mutable tank implementations must return a new instance.
     * @return The copied tank.
     */
    LimaFluidTank copy();

    @ApiStatus.Internal
    void setFluid(FluidStack stack);

    int fill(FluidStack resource, IFluidHandler.FluidAction action, boolean ignoreLimit);

    FluidStack drain(int maxDrain, IFluidHandler.FluidAction action, boolean ignoreLimit);

    FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action, boolean ignoreLimit);

    @Override
    default int fill(FluidStack resource, IFluidHandler.FluidAction action)
    {
        return fill(resource, action, false);
    }

    @Override
    default FluidStack drain(int maxDrain, IFluidHandler.FluidAction action)
    {
        return drain(maxDrain, action, false);
    }

    @Override
    default FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action)
    {
        return drain(resource, action, false);
    }
}