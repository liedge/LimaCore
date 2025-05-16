package liedge.limacore.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import org.jetbrains.annotations.Nullable;

public record SpriteParticleProvider<T extends ParticleOptions>(SpriteSet spriteSet, ParticleFactory<T> factory) implements ParticleProvider<T>
{
    @Nullable
    @Override
    public Particle createParticle(T type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        return factory.create(type, level, spriteSet, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    @FunctionalInterface
    public interface ParticleFactory<T extends ParticleOptions>
    {
        @Nullable
        Particle create(T type, ClientLevel level, SpriteSet spriteSet, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed);
    }

    @FunctionalInterface
    public interface PositionOnlyParticleFactory<T extends ParticleOptions> extends ParticleFactory<T>
    {
        @Override
        @Nullable
        default Particle create(T type, ClientLevel level, SpriteSet spriteSet, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            return create(type, level, spriteSet, x, y, z);
        }

        @Nullable
        Particle create(T type, ClientLevel level, SpriteSet spriteSet, double x, double y, double z);
    }
}