package liedge.limacore.menu.slot;

import liedge.limacore.transfer.fluid.LimaBlockEntityFluids;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public record LimaFluidSlot(LimaBlockEntityFluids fluids, int slot, int resourceIndex, int x, int y, boolean allowInsert)
{
    public FluidStack getFluid()
    {
        return fluids.getResource(resourceIndex).toStack(fluids.getAmountAsInt(resourceIndex));
    }

    public int getCapacity()
    {
        return fluids.getCapacity();
    }

    public boolean mayPlace(FluidResource resource)
    {
        return allowInsert && fluids.isValid(resourceIndex, resource);
    }

    public enum ClickAction
    {
        FILL,
        DRAIN;

        public static final StreamCodec<FriendlyByteBuf, ClickAction> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(ClickAction.class);
    }
}