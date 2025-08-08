package liedge.limacore.menu.slot;

import liedge.limacore.capability.fluid.LimaFluidHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record LimaFluidSlot(LimaFluidHandler fluidHandler, int index, int tank, int x, int y, boolean allowInsert)
{
    public FluidStack getFluid()
    {
        return fluidHandler.getFluidInTank(tank);
    }

    public int getCapacity()
    {
        return fluidHandler.getTankCapacity(tank);
    }

    public boolean mayPlace(FluidStack stack)
    {
        return allowInsert && fluidHandler.isFluidValid(tank, stack);
    }

    public enum ClickAction
    {
        FILL,
        DRAIN;

        public static final StreamCodec<FriendlyByteBuf, ClickAction> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(ClickAction.class);
    }
}