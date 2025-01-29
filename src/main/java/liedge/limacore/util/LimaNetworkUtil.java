package liedge.limacore.util;

import liedge.limacore.lib.function.Consumer3;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.function.Supplier;

public final class LimaNetworkUtil
{
    private LimaNetworkUtil() {}

    public static void spawnAlwaysVisibleParticle(Level level, ParticleOptions options, Vec3 location, Vec3 speed)
    {
        if (level instanceof ServerLevel serverLevel)
        {
            ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(options, true, location.x, location.y, location.z, (float) speed.x, (float) speed.y, (float) speed.z, 1, 0);
            for (ServerPlayer player : serverLevel.players())
            {
                if (player.blockPosition().closerToCenterThan(location, 512d))
                {
                    player.connection.send(packet);
                }
            }
        }
    }

    public static void spawnAlwaysVisibleParticle(Level level, ParticleOptions options, Vec3 location)
    {
        spawnAlwaysVisibleParticle(level, options, location, Vec3.ZERO);
    }

    public static void spawnAlwaysVisibleParticle(Level level, Supplier<? extends SimpleParticleType> typeSupplier, Vec3 location, Vec3 speed)
    {
        spawnAlwaysVisibleParticle(level, typeSupplier.get(), location, speed);
    }

    public static void spawnAlwaysVisibleParticle(Level level, Supplier<? extends SimpleParticleType> typeSupplier, Vec3 location)
    {
        spawnAlwaysVisibleParticle(level, typeSupplier, location, Vec3.ZERO);
    }

    public static <T extends CustomPacketPayload> IPayloadHandler<T> serverPacketHandler(Consumer3<T, IPayloadContext, ServerPlayer> function)
    {
        return (payload, context) -> function.accept(payload, context, LimaCoreUtil.castOrThrow(ServerPlayer.class, context.player(), "Cannot accept server packet without sender."));
    }
}