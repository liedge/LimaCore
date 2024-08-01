package liedge.limacore.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SpriteSetParticleProvider<T extends ParticleOptions> implements ParticleProvider<T>
{
    public static <T extends ParticleOptions> void registerPositionVelocity(RegisterParticleProvidersEvent event, Supplier<? extends ParticleType<T>> supplier, PositionVelocityParticleFactory<T> factory)
    {
        event.registerSpriteSet(supplier.get(), spriteSet -> new SpriteSetParticleProvider<>(spriteSet, factory));
    }

    public static <T extends ParticleOptions> void registerPositionOnly(RegisterParticleProvidersEvent event, Supplier<? extends ParticleType<T>> supplier, PositionParticleFactory<T> factory)
    {
        registerPositionVelocity(event, supplier, factory);
    }

    private final SpriteSet spriteSet;
    private final PositionVelocityParticleFactory<T> factory;

    public SpriteSetParticleProvider(SpriteSet spriteSet, PositionVelocityParticleFactory<T> factory)
    {
        this.spriteSet = spriteSet;
        this.factory = factory;
    }

    @Override
    public @Nullable Particle createParticle(T type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz)
    {
        return factory.newParticle(type, level, spriteSet, x, y, z, dx, dy, dz);
    }

    @FunctionalInterface
    public interface PositionVelocityParticleFactory<T extends ParticleOptions>
    {
        @Nullable Particle newParticle(T type, ClientLevel level, SpriteSet spriteSet, double x, double y, double z, double dx, double dy, double dz);
    }

    @FunctionalInterface
    public interface PositionParticleFactory<T extends ParticleOptions> extends PositionVelocityParticleFactory<T>
    {
        @Override
        default @Nullable Particle newParticle(T type, ClientLevel level, SpriteSet spriteSet, double x, double y, double z, double dx, double dy, double dz)
        {
            return newParticle(type, level, spriteSet, x, y, z);
        }

        @Nullable Particle newParticle(T type, ClientLevel level, SpriteSet spriteSet, double x, double y, double z);
    }
}