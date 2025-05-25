package liedge.limacore.network.packet;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static liedge.limacore.util.LimaNetworkUtil.serverPacketHandler;

public final class LimaCorePackets
{
    public static void registerPacketHandlers(PayloadRegistrar registrar)
    {
        registrar.playToClient(ClientboundBlockEntityDataWatcherPacket.TYPE, ClientboundBlockEntityDataWatcherPacket.STREAM_CODEC, LimaCoreClientPacketHandler::handleBlockDataWatcherPacket);
        registrar.playToClient(ClientboundMenuDataWatcherPacket.TYPE, ClientboundMenuDataWatcherPacket.STREAM_CODEC, LimaCoreClientPacketHandler::handleMenuDataWatcherPacket);
        registrar.playToClient(ClientboundParticlePacket.TYPE, ClientboundParticlePacket.STREAM_CODEC, LimaCoreClientPacketHandler::handleParticlePacket);

        registrar.playToServer(ServerboundCustomMenuButtonPacket.TYPE, ServerboundCustomMenuButtonPacket.STREAM_CODEC, serverPacketHandler(LimaCoreServerPacketHandler::handleCustomMenuButtonPacket));
        registrar.playToServer(ServerboundBlockEntityDataRequestPacket.TYPE, ServerboundBlockEntityDataRequestPacket.STREAM_CODEC, serverPacketHandler(LimaCoreServerPacketHandler::handleBlockDataRequestPacket));
    }
}