package liedge.limacore.client.particle;

import liedge.limacore.lib.LimaColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class LimaParticleUtil
{
    private LimaParticleUtil() {}

    public static void setColor(Particle particle, LimaColor color)
    {
        particle.setColor(color.red(), color.green(), color.blue());
    }

    public static <T extends ParticleOptions> void registerSpecialPosOnly(RegisterParticleProvidersEvent event, Supplier<? extends ParticleType<T>> typeSupplier, PositionOnlyParticleProvider<T> factory)
    {
        event.registerSpecial(typeSupplier.get(), factory);
    }

    public static <T extends ParticleOptions> void registerSprites(RegisterParticleProvidersEvent event, Supplier<? extends ParticleType<T>> typeSupplier, SpriteParticleProvider.ParticleFactory<T> factory)
    {
        event.registerSpriteSet(typeSupplier.get(), set -> new SpriteParticleProvider<>(set, factory));
    }

    public static <T extends ParticleOptions> void registerSpritesPosOnly(RegisterParticleProvidersEvent event, Supplier<? extends ParticleType<T>> typeSupplier, SpriteParticleProvider.PositionOnlyParticleFactory<T> factory)
    {
        event.registerSpriteSet(typeSupplier.get(), set -> new SpriteParticleProvider<>(set, factory));
    }

    public interface PositionOnlyParticleProvider<T extends ParticleOptions> extends ParticleProvider<T>
    {
        @Override
        default Particle createParticle(T type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            return createParticle(type, level, x, y, z);
        }

        @Nullable Particle createParticle(T type, ClientLevel level, double x, double y, double z);
    }
}