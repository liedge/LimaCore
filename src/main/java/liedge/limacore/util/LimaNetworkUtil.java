package liedge.limacore.util;

import liedge.limacore.network.ClientboundPayload;
import liedge.limacore.network.ServerboundPayload;
import liedge.limacore.network.packet.ClientboundParticlePacket;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class LimaNetworkUtil
{
    private LimaNetworkUtil() {}

    //#region Packet helpers
    public static void sendParticle(ServerLevel level, ParticleOptions data, boolean overrideLimiter, boolean alwaysShow, Vec3 pos, Vec3 aux)
    {
        double maxDist = overrideLimiter ? 512d : 32d;
        PacketDistributor.sendToPlayersNear(level, null, pos.x, pos.y, pos.z, maxDist, new ClientboundParticlePacket(data, pos, aux, overrideLimiter, alwaysShow));
    }

    public static void sendParticle(ServerLevel level, ParticleOptions data, boolean overrideLimiter, boolean alwaysShow, double x, double y, double z, double xAux, double yAux, double zAux)
    {
        sendParticle(level, data, overrideLimiter, alwaysShow, new Vec3(x, y, z), new Vec3(xAux, yAux, zAux));
    }

    public static void sendParticle(ServerLevel level, ParticleOptions data, boolean overrideLimiter, boolean alwaysShow, Vec3 pos)
    {
        sendParticle(level, data, overrideLimiter, alwaysShow, pos, Vec3.ZERO);
    }

    public static void sendParticle(ServerLevel level, ParticleOptions data, boolean overrideLimiter, boolean alwaysShow, double x, double y, double z)
    {
        sendParticle(level, data, overrideLimiter, alwaysShow, x, y, z, 0, 0, 0);
    }

    public static void sendAlwaysVisibleParticle(ServerLevel level, ParticleOptions data, Vec3 pos, Vec3 aux)
    {
        sendParticle(level, data, true, true, pos, aux);
    }

    public static void sendAlwaysVisibleParticle(ServerLevel level, ParticleOptions data, double x, double y, double z, double xAux, double yAux, double zAux)
    {
        sendParticle(level, data, true, true, x, y, z, xAux, yAux, zAux);
    }

    public static void sendAlwaysVisibleParticle(ServerLevel level, ParticleOptions data, Vec3 pos)
    {
        sendAlwaysVisibleParticle(level, data, pos, Vec3.ZERO);
    }

    public static void sendAlwaysVisibleParticle(ServerLevel level, ParticleOptions data, double x, double y, double z)
    {
        sendAlwaysVisibleParticle(level, data, x, y, z, 0, 0, 0);
    }
    //#endregion

    public static <T extends ClientboundPayload> void registerPlayToClient(PayloadRegistrar registrar, CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec)
    {
        registrar.playToClient(type, streamCodec, ClientboundPayload::handleClient);
    }

    public static <T extends ServerboundPayload> IPayloadHandler<T> serverPayloadHandler()
    {
        return (payload, context) ->
        {
            ServerPlayer sender = LimaCoreObjects.cast(ServerPlayer.class, context.player(), "Received server packet without sender.");
            payload.handleServer(sender, context);
        };
    }

    public static <T extends ServerboundPayload> void registerPlayToServer(PayloadRegistrar registrar, CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec)
    {
        registrar.playToServer(type, streamCodec, serverPayloadHandler());
    }

    public static <T extends ClientboundPayload & ServerboundPayload> void registerBiDirectional(PayloadRegistrar registrar, CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec)
    {
        registrar.playBidirectional(type, streamCodec, serverPayloadHandler(), ClientboundPayload::handleClient);
    }
}