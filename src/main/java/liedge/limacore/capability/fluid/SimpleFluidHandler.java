package liedge.limacore.capability.fluid;

import com.google.common.base.Preconditions;
import net.minecraft.core.NonNullList;

public final class SimpleFluidHandler extends LimaFluidHandler
{
    private final NonNullList<? extends LimaFluidTank> tanks;

    public SimpleFluidHandler(NonNullList<? extends LimaFluidTank> tanks)
    {
        Preconditions.checkArgument(!tanks.isEmpty(), "Fluid handler must have at least 1 tank.");
        this.tanks = tanks;
    }

    @Override
    protected NonNullList<? extends LimaFluidTank> getFluidTanks()
    {
        return tanks;
    }
}