package liedge.limacore.capability.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class CombinedFluidsWrapper implements IFluidHandler
{
    private final IFluidHandler[] handlers;
    private final int[] indexOffsets;
    private final int tanks;

    public CombinedFluidsWrapper(IFluidHandler... handlers)
    {
        this.handlers = handlers;
        this.indexOffsets = new int[handlers.length];
        int index = 0;
        for (int i = 0; i < handlers.length; i++)
        {
            index += handlers[i].getTanks();
            indexOffsets[i] = index;
        }
        this.tanks = index;
    }

    private int resolveHandlerIndex(int tank)
    {
        for (int i = 0; i < indexOffsets.length; i++)
        {
            if (tank < indexOffsets[i])
            {
                return i;
            }
        }

        throw new IndexOutOfBoundsException("Tank " + tank + " not in valid range [0," + tanks + ")");
    }

    private int resolveTankIndex(int tank, int handlerIndex)
    {
        if (handlerIndex == 0) return tank;
        return tank - indexOffsets[handlerIndex - 1];
    }

    @Override
    public int getTanks()
    {
        return tanks;
    }

    @Override
    public FluidStack getFluidInTank(int tank)
    {
        int handlerIndex = resolveHandlerIndex(tank);
        tank = resolveTankIndex(tank, handlerIndex);
        return handlers[handlerIndex].getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank)
    {
        int handlerIndex = resolveHandlerIndex(tank);
        tank = resolveTankIndex(tank, handlerIndex);
        return handlers[handlerIndex].getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack)
    {
        int handlerIndex = resolveHandlerIndex(tank);
        tank = resolveTankIndex(tank, handlerIndex);
        return handlers[handlerIndex].isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        int result = 0;

        for (IFluidHandler handler : handlers)
        {
            result = handler.fill(resource, action);
            if (result > 0) break;
        }

        return result;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        FluidStack result = FluidStack.EMPTY;

        for (IFluidHandler handler : handlers)
        {
            result = handler.drain(resource, action);
            if (!result.isEmpty()) break;
        }

        return result;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        FluidStack result = FluidStack.EMPTY;

        for (IFluidHandler handler : handlers)
        {
            result = handler.drain(maxDrain, action);
            if (!result.isEmpty()) break;
        }

        return result;
    }
}