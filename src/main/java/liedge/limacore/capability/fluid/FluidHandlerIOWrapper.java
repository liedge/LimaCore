package liedge.limacore.capability.fluid;

import liedge.limacore.blockentity.IOAccess;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public interface FluidHandlerIOWrapper extends IFluidHandler
{
    static FluidHandlerIOWrapper of(IFluidHandler handler, IOAccess access)
    {
        return new SimpleWrapper(handler, access);
    }

    IFluidHandler fluidHandler();

    @Override
    default int getTanks()
    {
        return fluidHandler().getTanks();
    }

    @Override
    default FluidStack getFluidInTank(int tank)
    {
        return fluidHandler().getFluidInTank(tank);
    }

    @Override
    default int getTankCapacity(int tank)
    {
        return fluidHandler().getTankCapacity(tank);
    }

    @Override
    default boolean isFluidValid(int tank, FluidStack stack)
    {
        return fluidHandler().isFluidValid(tank, stack);
    }

    record SimpleWrapper(IFluidHandler fluidHandler, IOAccess access) implements FluidHandlerIOWrapper
    {
        @Override
        public int fill(FluidStack resource, FluidAction action)
        {
            return access.allowsInput() ? fluidHandler.fill(resource, action) : 0;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action)
        {
            return access.allowsOutput() ? fluidHandler.drain(resource, action) : FluidStack.EMPTY;
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action)
        {
            return access.allowsOutput() ? fluidHandler.drain(maxDrain, action) : FluidStack.EMPTY;
        }
    }
}