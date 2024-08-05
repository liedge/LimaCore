package liedge.limacore.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import liedge.limacore.lib.LimaColor;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

public record ColorSizeParticleOptions(ParticleType<ColorSizeParticleOptions> type, LimaColor color, float size) implements ParticleOptions
{
    private static MapCodec<ColorSizeParticleOptions> makeMapCodec(ParticleType<ColorSizeParticleOptions> type)
    {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                LimaColor.CODEC.fieldOf("color").forGetter(ColorSizeParticleOptions::color),
                Codec.FLOAT.fieldOf("size").forGetter(ColorSizeParticleOptions::size))
                .apply(instance, (color, size) -> new ColorSizeParticleOptions(type, color, size)));
    }

    private static StreamCodec<ByteBuf, ColorSizeParticleOptions> makeStreamCodec(ParticleType<ColorSizeParticleOptions> type)
    {
        return StreamCodec.composite(
                LimaColor.STREAM_CODEC, ColorSizeParticleOptions::color,
                ByteBufCodecs.FLOAT, ColorSizeParticleOptions::size,
                (color, size) -> new ColorSizeParticleOptions(type, color, size));
    }

    public static LimaParticleType<ColorSizeParticleOptions> createParticleType(boolean overrideLimiter)
    {
        return LimaParticleType.createWithTypedCodecs(overrideLimiter, ColorSizeParticleOptions::makeMapCodec, ColorSizeParticleOptions::makeStreamCodec);
    }

    public ColorSizeParticleOptions(Supplier<? extends ParticleType<ColorSizeParticleOptions>> typeSupplier, LimaColor color, float size)
    {
        this(typeSupplier.get(), color, size);
    }

    @Override
    public ParticleType<?> getType()
    {
        return type;
    }
}