package liedge.limacore.capability.fluid;

import net.minecraft.core.NonNullList;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/**
 * An extension of {@link IFluidHandler} meant for use with {@link LimaFluidTank} instances. Provides extended
 * fluid operations with finer control for transfer limits and IO direction limitations.
 */
public abstract class LimaFluidHandler implements IFluidHandler
{
    public LimaFluidTank getFluidTank(int tank) throws IndexOutOfBoundsException
    {
        validateTankIndex(tank);
        return getFluidTanks().get(tank);
    }

    protected abstract NonNullList<? extends LimaFluidTank> getFluidTanks();

    private void validateTankIndex(int tank)
    {
        if (tank < 0 || tank >= getTanks())
            throw new IndexOutOfBoundsException(String.format("Tank index %s out of valid range [0,%s)", tank, getTanks()));
    }

    protected void onTankChanged(int tank) {}

    @Override
    public int getTanks()
    {
        return getFluidTanks().size();
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
        return getFluidTank(tank).isFluidValid(stack);
    }

    public int fillTank(int tank, FluidStack resource, FluidAction action, boolean ignoreLimit)
    {
        int filled = getFluidTank(tank).fill(resource, action, ignoreLimit);
        if (filled == 0) return 0;
        if (!action.simulate()) onTankChanged(tank);

        return filled;
    }

    public FluidStack drainTank(int tank, FluidStack resource, FluidAction action, boolean ignoreLimit)
    {
        FluidStack drained = getFluidTank(tank).drain(resource, action, ignoreLimit);
        if (drained.isEmpty()) return FluidStack.EMPTY;
        if (!action.simulate()) onTankChanged(tank);

        return drained;
    }

    public FluidStack drainTank(int tank, int maxDrain, FluidAction action, boolean ignoreLimit)
    {
        FluidStack drained = getFluidTank(tank).drain(maxDrain, action, ignoreLimit);
        if (drained.isEmpty()) return FluidStack.EMPTY;
        if (!action.simulate()) onTankChanged(tank);

        return drained;
    }

    public int fillAny(FluidStack resource, FluidAction action, boolean ignoreLimit)
    {
        FluidStack remaining = resource.copy();
        int filled = 0;

        for (int i = 0; i < getTanks(); i++)
        {
            int accepted = fillTank(i, remaining, action, ignoreLimit);

            if (accepted > 0)
            {
                remaining.shrink(accepted);
                filled += accepted;
                if (remaining.isEmpty()) break;
            }
        }

        return filled;
    }

    public FluidStack drainFromAny(FluidStack resource, FluidAction action, boolean ignoreLimit)
    {
        if (resource.isEmpty()) return FluidStack.EMPTY;

        FluidStack result = FluidStack.EMPTY;
        FluidStack remaining = resource.copy();

        for (int i = 0; i < getTanks(); i++)
        {
            FluidStack accepted = drainTank(i, remaining, action, ignoreLimit);
            if (!accepted.isEmpty())
            {
                remaining.shrink(accepted.getAmount());

                if (result.isEmpty())
                    result = accepted.copy();
                else
                    result.grow(accepted.getAmount());

                if (remaining.isEmpty()) break;
            }
        }

        return result;
    }

    public int fillFirst(FluidStack resource, FluidAction action, boolean ignoreLimit)
    {
        for (int i = 0; i < getTanks(); i++)
        {
            int filled = fillTank(i, resource, action, ignoreLimit);
            if (filled > 0) return filled;
        }

        return 0;
    }

    public FluidStack drainFromFirst(FluidStack resource, FluidAction action, boolean ignoreLimit)
    {
        for (int i = 0; i < getTanks(); i++)
        {
            FluidStack drained = drainTank(i, resource, action, ignoreLimit);
            if (!drained.isEmpty()) return drained;
        }

        return FluidStack.EMPTY;
    }

    public FluidStack drainFromFirst(int maxDrain, FluidAction action, boolean ignoreLimit)
    {
        for (int i = 0; i < getTanks(); i++)
        {
            FluidStack drained = drainTank(i, maxDrain, action, ignoreLimit);
            if (!drained.isEmpty()) return drained;
        }

        return FluidStack.EMPTY;
    }

    @Deprecated
    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        return fillFirst(resource, action, false);
    }

    @Deprecated
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        return drainFromFirst(resource, action, false);
    }

    @Deprecated
    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        return drainFromFirst(maxDrain, action, false);
    }
}