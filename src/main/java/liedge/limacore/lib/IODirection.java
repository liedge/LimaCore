package liedge.limacore.lib;

import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum IODirection implements StringRepresentable
{
    INPUT_ONLY("input", true, false),
    OUTPUT_ONLY("output", false, true),
    BOTH("both", true, true),
    NONE("none", false, false);

    public static final LimaEnumCodec<IODirection> CODEC = LimaEnumCodec.createDefaulted(IODirection.class, BOTH);
    public static final StreamCodec<FriendlyByteBuf, IODirection> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(IODirection.class);

    private final String name;
    private final boolean input;
    private final boolean output;

    IODirection(String name, boolean input, boolean output)
    {
        this.name = name;
        this.input = input;
        this.output = output;
    }

    public boolean allowsInput()
    {
        return input;
    }

    public boolean allowsOutput()
    {
        return output;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}