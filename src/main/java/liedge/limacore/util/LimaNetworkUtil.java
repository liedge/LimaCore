package liedge.limacore.util;

import liedge.limacore.lib.function.Consumer3;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class LimaNetworkUtil
{
    private LimaNetworkUtil() {}

    public static final int NORMAL_PARTICLE_DIST = 32;
    public static final int LONG_PARTICLE_DIST = 100;
    public static final int UNLIMITED_PARTICLE_DIST = 512;

    public static void sendSingleParticle(Level level, ParticleOptions options, @Nullable Player player, boolean overrideLimiter, double maxDistance, double x, double y, double z, float xSpeed, float ySpeed, float zSpeed)
    {
        if (level instanceof ServerLevel serverLevel)
        {
            ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(options, overrideLimiter, x, y, z, xSpeed, ySpeed, zSpeed, 1, 0);
            for (ServerPlayer serverPlayer : serverLevel.players())
            {
                if (serverPlayer == player || serverPlayer.blockPosition().distToCenterSqr(x, y, z) < Mth.square(maxDistance))
                    serverPlayer.connection.send(packet);
            }
        }
    }

    public static void sendSingleParticle(Level level, Supplier<? extends SimpleParticleType> typeSupplier, @Nullable Player player, boolean overrideLimiter, double maxDistance, double x, double y, double z, float xSpeed, float ySpeed, float zSpeed)
    {
        sendSingleParticle(level, typeSupplier.get(), player, overrideLimiter, maxDistance, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    public static void sendSingleParticle(Level level, ParticleOptions options, @Nullable Player player, boolean overrideLimiter, double maxDistance, double x, double y, double z)
    {
        sendSingleParticle(level, options, player, overrideLimiter, maxDistance, x, y, z, 0f, 0f, 0f);
    }

    public static void sendSingleParticle(Level level, Supplier<? extends SimpleParticleType> typeSupplier, @Nullable Player player, boolean overrideLimiter, double maxDistance, double x, double y, double z)
    {
        sendSingleParticle(level, typeSupplier.get(), player, overrideLimiter, maxDistance, x, y, z);
    }

    public static void sendSingleParticle(Level level, ParticleOptions options, @Nullable Player player, boolean overrideLimiter, double maxDistance, Vec3 pos, Vec3 speed)
    {
        sendSingleParticle(level, options, player, overrideLimiter, maxDistance, pos.x, pos.y, pos.z, (float)speed.x, (float)speed.y, (float)speed.z);
    }

    public static void sendSingleParticle(Level level, Supplier<? extends SimpleParticleType> typeSupplier, @Nullable Player player, boolean overrideLimiter, double maxDistance, Vec3 pos, Vec3 speed)
    {
        sendSingleParticle(level, typeSupplier.get(), player, overrideLimiter, maxDistance, pos, speed);
    }

    public static void sendSingleParticle(Level level, ParticleOptions options, @Nullable Player player, boolean overrideLimiter, double maxDistance, Vec3 pos)
    {
        sendSingleParticle(level, options, player, overrideLimiter, maxDistance, pos.x, pos.y, pos.z, 0f, 0f, 0f);
    }

    public static void sendSingleParticle(Level level, Supplier<? extends SimpleParticleType> typeSupplier, @Nullable Player player, boolean overrideLimiter, double maxDistance, Vec3 pos)
    {
        sendSingleParticle(level, typeSupplier.get(), player, overrideLimiter, maxDistance, pos);
    }

    public static <T extends CustomPacketPayload> IPayloadHandler<T> serverPacketHandler(Consumer3<T, IPayloadContext, ServerPlayer> function)
    {
        return (payload, context) -> function.accept(payload, context, LimaCoreUtil.castOrThrow(ServerPlayer.class, context.player(), "Cannot accept server packet without sender."));
    }
}