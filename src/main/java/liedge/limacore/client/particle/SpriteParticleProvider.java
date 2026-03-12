package liedge.limacore.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public record SpriteParticleProvider<T extends ParticleOptions>(SpriteSet sprites, Factory<T> factory) implements ParticleProvider<T>
{
    public static <T extends ParticleOptions> void register(RegisterParticleProvidersEvent event, Supplier<? extends ParticleType<T>> typeSupplier, Factory<T> factory)
    {
        event.registerSpriteSet(typeSupplier.get(), sprites -> new SpriteParticleProvider<>(sprites, factory));
    }

    @Override
    public @Nullable Particle createParticle(T particleType, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random)
    {
        return factory.create(particleType, level, sprites, x, y, z, xSpeed, ySpeed, zSpeed, random);
    }

    @FunctionalInterface
    public interface Factory<T extends ParticleOptions>
    {
        @Nullable
        Particle create(T type, ClientLevel level, SpriteSet sprites, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random);
    }
}