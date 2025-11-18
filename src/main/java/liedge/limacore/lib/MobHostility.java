package liedge.limacore.lib;

import com.mojang.serialization.Codec;
import liedge.limacore.advancement.ComparableBounds;
import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum MobHostility implements StringRepresentable
{
    PASSIVE("passive"),
    NEUTRAL_MOB("neutral_mob"),
    NEUTRAL_ENEMY("neutral_enemy"),
    HOSTILE("hostile");

    public static final LimaEnumCodec<MobHostility> CODEC = LimaEnumCodec.create(MobHostility.class);
    public static final StreamCodec<FriendlyByteBuf, MobHostility> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(MobHostility.class);
    public static final Codec<ComparableBounds<MobHostility>> BOUNDS_CODEC = ComparableBounds.codec(CODEC);

    private final String name;

    MobHostility(String name)
    {
        this.name = name;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }

    public boolean atMost(MobHostility other)
    {
        return this.compareTo(other) <= 0;
    }

    public boolean atLeast(MobHostility other)
    {
        return this.compareTo(other) >= 0;
    }

    public boolean between(MobHostility min, MobHostility max)
    {
        return this.atLeast(min) && this.atMost(max);
    }
}