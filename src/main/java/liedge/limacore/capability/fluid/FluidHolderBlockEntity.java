package liedge.limacore.capability.fluid;

import net.neoforged.neoforge.fluids.FluidStack;

public interface FluidHolderBlockEntity
{
    boolean isValidFluid(int tank, FluidStack fluidStack);

    int getBaseFluidCapacity(int tank);

    void onFluidsChanged(int tank);
}