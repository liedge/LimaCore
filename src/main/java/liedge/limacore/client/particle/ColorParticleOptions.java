package liedge.limacore.client.particle;

import liedge.limacore.lib.LimaColor;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Supplier;

public record ColorParticleOptions(ParticleType<ColorParticleOptions> type, LimaColor color) implements ParticleOptions
{
    public static LimaParticleType<ColorParticleOptions> createParticleType(boolean overrideLimiter)
    {
        return LimaParticleType.createWithTypedCodecs(overrideLimiter,
                type -> LimaColor.CODEC.fieldOf("color").xmap(color -> new ColorParticleOptions(type, color), ColorParticleOptions::color),
                type -> LimaColor.STREAM_CODEC.map(color -> new ColorParticleOptions(type, color), ColorParticleOptions::color));
    }

    public ColorParticleOptions(Supplier<? extends ParticleType<ColorParticleOptions>> typeSupplier, LimaColor color)
    {
        this(typeSupplier.get(), color);
    }

    @Override
    public ParticleType<?> getType()
    {
        return type;
    }
}