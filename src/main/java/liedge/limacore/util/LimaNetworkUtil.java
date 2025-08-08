package liedge.limacore.util;

import liedge.limacore.network.ClientboundPayload;
import liedge.limacore.network.ServerboundPayload;
import liedge.limacore.network.packet.ClientboundParticlePacket;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.function.Supplier;

public final class LimaNetworkUtil
{
    private LimaNetworkUtil() {}

    public static final int NORMAL_PARTICLE_DIST = 32;
    public static final int LONG_PARTICLE_DIST = 100;
    public static final int UNLIMITED_PARTICLE_DIST = 512;

    //#region Packet helpers
    public static void sendParticle(Level level, ParticleOptions options, double maxDistance, Vec3 pos, Vec3 speed)
    {
        if (level instanceof ServerLevel serverLevel)
        {
            PacketDistributor.sendToPlayersNear(serverLevel, null, pos.x, pos.y, pos.z, maxDistance, new ClientboundParticlePacket(options, pos, speed));
        }
    }

    public static void sendParticle(Level level, Supplier<? extends SimpleParticleType> typeSupplier, double maxDistance, Vec3 pos, Vec3 speed)
    {
        sendParticle(level, typeSupplier.get(), maxDistance, pos, speed);
    }

    public static void sendParticle(Level level, ParticleOptions options, double maxDistance, Vec3 pos)
    {
        sendParticle(level, options, maxDistance, pos, Vec3.ZERO);
    }

    public static void sendParticle(Level level, Supplier<? extends SimpleParticleType> typeSupplier, double maxDistance, Vec3 pos)
    {
        sendParticle(level, typeSupplier.get(), maxDistance, pos);
    }

    public static void sendParticle(Level level, ParticleOptions options, double maxDistance, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        sendParticle(level, options, maxDistance, new Vec3(x, y, z), new Vec3(xSpeed, ySpeed, zSpeed));
    }

    public static void sendParticle(Level level, Supplier<? extends SimpleParticleType> typeSupplier, double maxDistance, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        sendParticle(level, typeSupplier.get(), maxDistance, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    public static void sendParticle(Level level, ParticleOptions options, double maxDistance, double x, double y, double z)
    {
        sendParticle(level, options, maxDistance, new Vec3(x, y, z));
    }

    public static void sendParticle(Level level, Supplier<? extends SimpleParticleType> typeSupplier, double maxDistance, double x, double y, double z)
    {
        sendParticle(level, typeSupplier.get(), maxDistance, x, y, z);
    }
    //#endregion

    public static <T extends ClientboundPayload> void registerPlayToClient(PayloadRegistrar registrar, CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec)
    {
        registrar.playToClient(type, streamCodec, ClientboundPayload::handleClient);
    }

    public static <T extends ServerboundPayload> void registerPlayToServer(PayloadRegistrar registrar, CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec)
    {
        registrar.playToServer(type, streamCodec, (payload, context) ->
        {
            ServerPlayer sender = LimaCoreUtil.castOrThrow(ServerPlayer.class, context.player(), "Received server packet without sender.");
            payload.handleServer(sender, context);
        });
    }
}