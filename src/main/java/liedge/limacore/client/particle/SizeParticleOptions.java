package liedge.limacore.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

public record SizeParticleOptions(ParticleType<SizeParticleOptions> type, float size) implements ParticleOptions
{
    private static MapCodec<SizeParticleOptions> makeMapCodec(ParticleType<SizeParticleOptions> type)
    {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.FLOAT.fieldOf("size").forGetter(SizeParticleOptions::size))
                .apply(instance, size -> new SizeParticleOptions(type, size)));
    }

    private static StreamCodec<ByteBuf, SizeParticleOptions> makeStreamCodec(ParticleType<SizeParticleOptions> type)
    {
        return ByteBufCodecs.FLOAT.map(size -> new SizeParticleOptions(type, size), SizeParticleOptions::size);
    }

    public static LimaParticleType<SizeParticleOptions> createParticleType(boolean overrideLimiter)
    {
        return LimaParticleType.createWithTypedCodecs(overrideLimiter, SizeParticleOptions::makeMapCodec, SizeParticleOptions::makeStreamCodec);
    }

    public SizeParticleOptions(Supplier<? extends ParticleType<SizeParticleOptions>> typeSupplier, float size)
    {
        this(typeSupplier.get(), size);
    }

    @Override
    public ParticleType<?> getType()
    {
        return type;
    }
}