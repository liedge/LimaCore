package liedge.limacore.client.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.function.Supplier;

public record ColorParticleOptions(ParticleType<ColorParticleOptions> type, int color) implements BaseColorParticleOptions
{
    public static LimaParticleType<ColorParticleOptions> createParticleType(boolean overrideLimiter)
    {
        return LimaParticleType.createWithTypedCodecs(overrideLimiter,
                type -> COLOR_FIELD_CODEC.xmap(color -> new ColorParticleOptions(type, color), ColorParticleOptions::color),
                type -> ByteBufCodecs.VAR_INT.map(color -> new ColorParticleOptions(type, color), ColorParticleOptions::color));
    }

    public static ColorParticleOptions of(ParticleType<ColorParticleOptions> type, int color)
    {
        return new ColorParticleOptions(type, color);
    }

    public static ColorParticleOptions of(Supplier<? extends ParticleType<ColorParticleOptions>> typeSupplier, int color)
    {
        return of(typeSupplier.get(), color);
    }

    @Override
    public ParticleType<?> getType()
    {
        return type;
    }
}