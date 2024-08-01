package liedge.limacore.client.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public class LimaParticleType<T extends ParticleOptions> extends ParticleType<T>
{
    public static <T extends ParticleOptions> LimaParticleType<T> referenceCodecs(boolean overrideLimiter, Function<ParticleType<T>, MapCodec<T>> mapCodecFunction, Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecFunction)
    {
        return new LimaParticleType<>(overrideLimiter, mapCodecFunction, streamCodecFunction);
    }

    public static <T extends ParticleOptions> LimaParticleType<T> standaloneCodecs(boolean overrideLimiter, MapCodec<T> mapCodec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec)
    {
        return new LimaParticleType<>(overrideLimiter, ignored -> mapCodec, ignored -> streamCodec);
    }

    private final MapCodec<T> mapCodec;
    private final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;

    private LimaParticleType(boolean overrideLimiter, Function<ParticleType<T>, MapCodec<T>> mapCodecFunction, Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecFunction)
    {
        super(overrideLimiter);
        this.mapCodec = mapCodecFunction.apply(this);
        this.streamCodec = streamCodecFunction.apply(this);
    }

    @Override
    public MapCodec<T> codec()
    {
        return mapCodec;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec()
    {
        return streamCodec;
    }
}