package liedge.limacore.blockentity;

import liedge.limacore.LimaCore;
import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.OrderedEnum;
import liedge.limacore.lib.Translatable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.EnumSet;
import java.util.Set;

public enum IOAccess implements StringRepresentable, OrderedEnum<IOAccess>, Translatable
{
    DISABLED("disabled", false, false),
    INPUT_AND_OUTPUT("input_and_output", true, true),
    INPUT_ONLY("input_only", true, false),
    OUTPUT_ONLY("output_only", false, true);

    public static final LimaEnumCodec<IOAccess> CODEC = LimaEnumCodec.createLenient(IOAccess.class, DISABLED);
    public static final StreamCodec<FriendlyByteBuf, IOAccess> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(IOAccess.class);

    public static final Set<IOAccess> ALL_ALLOWED = EnumSet.allOf(IOAccess.class);
    public static final Set<IOAccess> INPUT_OR_OUTPUT_ONLY_AND_DISABLED = EnumSet.of(DISABLED, INPUT_ONLY, OUTPUT_ONLY);
    public static final Set<IOAccess> ONLY_INPUT_AND_DISABLED = EnumSet.of(DISABLED, INPUT_ONLY);
    public static final Set<IOAccess> ONLY_OUTPUT_AND_DISABLED = EnumSet.of(DISABLED, OUTPUT_ONLY);
    public static final Set<IOAccess> ONLY_BOTH_AND_DISABLED = EnumSet.of(DISABLED, INPUT_AND_OUTPUT);

    private final String name;
    private final String descriptionId;
    private final boolean inputEnabled;
    private final boolean outputEnabled;

    IOAccess(String name, boolean inputEnabled, boolean outputEnabled)
    {
        this.name = name;
        this.descriptionId = LimaCore.RESOURCES.translationKey("io_access", "{}", name);
        this.inputEnabled = inputEnabled;
        this.outputEnabled = outputEnabled;
    }

    public boolean allowsConnection()
    {
        return this != DISABLED;
    }

    public boolean allowsInput()
    {
        return inputEnabled;
    }

    public boolean allowsOutput()
    {
        return outputEnabled;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }

    @Override
    public String descriptionId()
    {
        return descriptionId;
    }
}