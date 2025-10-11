package liedge.limacore.lib.math;

import com.mojang.serialization.Codec;
import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;

public enum CompareOperation implements StringRepresentable
{
    EQUALS("equals"),
    GREATER_THAN("greater_than"),
    LESS_THAN("less_than"),
    GREATER_THAN_OR_EQUALS("greater_than_or_equals"),
    LESS_THAN_OR_EQUALS("less_than_or_equals");

    public static final Codec<CompareOperation> CODEC = LimaEnumCodec.create(CompareOperation.class);

    private final String name;

    CompareOperation(String name)
    {
        this.name = name;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }

    public boolean test(double left, double right)
    {
        return switch (this)
        {
            case EQUALS -> left == right;
            case GREATER_THAN -> left > right;
            case LESS_THAN -> left < right;
            case GREATER_THAN_OR_EQUALS -> left >= right;
            case LESS_THAN_OR_EQUALS -> left <= right;
        };
    }
}
