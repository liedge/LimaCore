package liedge.limacore.capability.fluid;

import liedge.limacore.blockentity.IOAccess;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidHandlerIOWrapper implements IFluidHandler
{
    private final LimaFluidHandler source;
    private final IOAccess ioAccess;

    public FluidHandlerIOWrapper(LimaFluidHandler source, IOAccess ioAccess)
    {
        this.source = source;
        this.ioAccess = ioAccess;
    }

    @Override
    public int getTanks()
    {
        return source.getTanks();
    }

    @Override
    public FluidStack getFluidInTank(int tank)
    {
        return source.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank)
    {
        return source.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack)
    {
        return source.isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        if (ioAccess.allowsInput())
        {
            for (int i = 0; i < getTanks(); i++)
            {
                if (source.getFluidTankIO(i).allowsInput())
                {
                    int filled = source.fillTank(i, resource, action, false);
                    if (filled > 0) return filled;
                }
            }
        }

        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        if (ioAccess.allowsOutput())
        {
            for (int i = 0; i < getTanks(); i++)
            {
                if (source.getFluidTankIO(i).allowsOutput())
                {
                    FluidStack drained = source.drainTank(i, resource, action, false);
                    if (!drained.isEmpty()) return drained;
                }
            }
        }

        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        if (ioAccess.allowsOutput())
        {
            for (int i = 0; i < getTanks(); i++)
            {
                if (source.getFluidTankIO(i).allowsOutput())
                {
                    FluidStack drained = source.drainTank(i, maxDrain, action, false);
                    if (!drained.isEmpty()) return drained;
                }
            }
        }

        return FluidStack.EMPTY;
    }
}