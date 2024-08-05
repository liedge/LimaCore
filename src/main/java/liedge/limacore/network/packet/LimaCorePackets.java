package liedge.limacore.network.packet;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class LimaCorePackets
{
    public static void registerPacketHandlers(PayloadRegistrar registrar)
    {
        ClientboundBlockEntityDataPacket.PACKET_SPEC.register(registrar);
        ClientboundMenuDataPacket.PACKET_SPEC.register(registrar);

        ServerboundCustomMenuButtonPacket.PACKET_SPEC.register(registrar);
        ServerboundBlockEntityDataRequestPacket.PACKET_SPEC.register(registrar);
    }
}