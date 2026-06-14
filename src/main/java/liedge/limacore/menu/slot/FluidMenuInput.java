package liedge.limacore.menu.slot;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum FluidMenuInput
{
    FILL,
    DRAIN,
    CLEAR,
    CLONE;

    public static final StreamCodec<FriendlyByteBuf, FluidMenuInput> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(FluidMenuInput.class);
}