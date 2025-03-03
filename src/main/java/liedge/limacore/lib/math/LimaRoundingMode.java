package liedge.limacore.lib.math;

import com.mojang.serialization.MapCodec;
import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;

public enum LimaRoundingMode implements StringRepresentable
{
    NATURAL("natural"),
    FLOOR("floor"),
    CEIL("ceil"),
    RANDOM("random");

    public static final LimaEnumCodec<LimaRoundingMode> CODEC = LimaEnumCodec.create(LimaRoundingMode.class);
    public static final MapCodec<LimaRoundingMode> NATURAL_OPTIONAL_MAP_CODEC = CODEC.optionalFieldOf("mode", NATURAL);

    private final String name;

    LimaRoundingMode(String name)
    {
        this.name = name;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}