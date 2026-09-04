package liedge.limacore.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.util.function.Supplier;

public record ColorSizeParticleOptions(ParticleType<ColorSizeParticleOptions> type, int color, float size) implements BaseColorParticleOptions
{
    private static MapCodec<ColorSizeParticleOptions> makeMapCodec(ParticleType<ColorSizeParticleOptions> type)
    {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                ExtraCodecs.RGB_COLOR_CODEC.fieldOf("color").forGetter(ColorSizeParticleOptions::color),
                Codec.FLOAT.fieldOf("size").forGetter(ColorSizeParticleOptions::size))
                .apply(instance, (color, size) -> new ColorSizeParticleOptions(type, color, size)));
    }

    private static StreamCodec<ByteBuf, ColorSizeParticleOptions> streamCodec(ParticleType<ColorSizeParticleOptions> type)
    {
        return StreamCodec.composite(
                ByteBufCodecs.VAR_INT, BaseColorParticleOptions::color,
                ByteBufCodecs.FLOAT, ColorSizeParticleOptions::size,
                (color, size) -> new ColorSizeParticleOptions(type, color, size));
    }

    public static LimaParticleType<ColorSizeParticleOptions> createParticleType(boolean overrideLimiter)
    {
        return LimaParticleType.createWithTypedCodecs(overrideLimiter, ColorSizeParticleOptions::makeMapCodec, ColorSizeParticleOptions::streamCodec);
    }

    public static ColorSizeParticleOptions of(ParticleType<ColorSizeParticleOptions> type, int color, float size)
    {
        return new ColorSizeParticleOptions(type, color, size);
    }

    public static ColorSizeParticleOptions of(Supplier<? extends ParticleType<ColorSizeParticleOptions>> typeSupplier, int color, float size)
    {
        return of(typeSupplier.get(), color, size);
    }

    @Override
    public ParticleType<?> getType()
    {
        return type;
    }
}