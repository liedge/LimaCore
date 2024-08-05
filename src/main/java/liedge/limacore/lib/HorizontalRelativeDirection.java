package liedge.limacore.lib;

import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum HorizontalRelativeDirection implements StringRepresentable
{
    FRONT("front"),
    LEFT("left"),
    RIGHT("right"),
    BACK("back");

    public static final LimaEnumCodec<HorizontalRelativeDirection> CODEC = LimaEnumCodec.createStrict(HorizontalRelativeDirection.class);
    public static final StreamCodec<FriendlyByteBuf, HorizontalRelativeDirection> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(HorizontalRelativeDirection.class);

    private final String name;

    HorizontalRelativeDirection(String name)
    {
        this.name = name;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}