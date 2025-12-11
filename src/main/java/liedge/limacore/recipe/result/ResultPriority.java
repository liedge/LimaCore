package liedge.limacore.recipe.result;

import liedge.limacore.LimaCore;
import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.Translatable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum ResultPriority implements StringRepresentable, Translatable
{
    PRIMARY("primary"),
    SECONDARY("secondary"),
    TERTIARY("tertiary");

    public static final LimaEnumCodec<ResultPriority> CODEC = LimaEnumCodec.create(ResultPriority.class);
    public static final StreamCodec<FriendlyByteBuf, ResultPriority> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(ResultPriority.class);

    private final String name;
    private final String translationKey;

    ResultPriority(String name)
    {
        this.name = name;
        this.translationKey = LimaCore.RESOURCES.translationKey("result_priority.{}", name);
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }

    @Override
    public String descriptionId()
    {
        return translationKey;
    }
}