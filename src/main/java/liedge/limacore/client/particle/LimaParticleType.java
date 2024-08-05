package liedge.limacore.client.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public class LimaParticleType<T extends ParticleOptions> extends ParticleType<T>
{
    /**
     * Creates a particle type with an existing particle option map codec and stream codec. For instances in which particle options use only 1 type.
     */
    public static <T extends ParticleOptions> LimaParticleType<T> create(boolean overrideLimiter, MapCodec<T> mapCodec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec)
    {
        return new LimaParticleType<>(overrideLimiter, ignored -> mapCodec, ignored -> streamCodec);
    }

    /**
     * Creates a particle type that supplies itself to the map codec and stream codec constructors. For instances in which particle options use more than 1 type and needs a unit codec reference to the type.
     */
    public static <T extends ParticleOptions> LimaParticleType<T> createWithTypedCodecs(boolean overrideLimiter, Function<ParticleType<T>, MapCodec<T>> mapCodecFunction, Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecFunction)
    {
        return new LimaParticleType<>(overrideLimiter, mapCodecFunction, streamCodecFunction);
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