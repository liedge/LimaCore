package liedge.limacore.capability.fluid;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntityAccess;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public interface FluidHolderBlockEntity extends LimaBlockEntityAccess
{
    LimaFluidHandler getFluidHandler();

    int getBaseFluidCapacity(int tank);

    int getBaseFluidTransferRate(int tank);

    boolean isValidFluid(int tank, FluidStack fluidStack);

    IOAccess getSideIOForFluids(Direction side);

    default IOAccess getFluidTankIO(int tank)
    {
        return IOAccess.INPUT_ONLY;
    }

    default void onFluidsChanged(int tank)
    {
        setChanged();
    }

    default @Nullable IFluidHandler createFluidIOWrapper(@Nullable Direction side)
    {
        return side != null ? new FluidHandlerIOWrapper(getFluidHandler(), getSideIOForFluids(side)) : null;
    }
}