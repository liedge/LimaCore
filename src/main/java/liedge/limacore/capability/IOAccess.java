package liedge.limacore.capability;

import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum IOAccess implements StringRepresentable
{
    DISABLED("disabled", false, false),
    INPUT_AND_OUTPUT("input_output", true, true),
    INPUT_ONLY("input_only", true, false),
    OUTPUT_ONLY("output_only", false, true);

    public static final LimaEnumCodec<IOAccess> CODEC = LimaEnumCodec.createDefaulted(IOAccess.class, DISABLED);
    public static final StreamCodec<FriendlyByteBuf, IOAccess> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(IOAccess.class);

    private final String name;
    private final boolean inputEnabled;
    private final boolean outputEnabled;

    IOAccess(String name, boolean inputEnabled, boolean outputEnabled)
    {
        this.name = name;
        this.inputEnabled = inputEnabled;
        this.outputEnabled = outputEnabled;
    }

    public boolean isInputEnabled()
    {
        return inputEnabled;
    }

    public boolean isOutputEnabled()
    {
        return outputEnabled;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}