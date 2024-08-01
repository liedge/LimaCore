package liedge.limacore.network.packet;

import net.minecraft.network.protocol.PacketFlow;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class LimaCorePackets
{
    public static void registerPacketHandlers(PayloadRegistrar registrar)
    {
        ClientboundBlockEntityDataPacket.PACKET_SPEC.registerPacket(registrar, PacketFlow.CLIENTBOUND);
        ClientboundMenuDataPacket.PACKET_SPEC.registerPacket(registrar, PacketFlow.CLIENTBOUND);

        ServerboundLimaMenuButtonPacket.PACKET_SPEC.registerPacket(registrar, PacketFlow.SERVERBOUND);
        ServerboundBlockEntityDataRequestPacket.PACKET_SPEC.registerPacket(registrar, PacketFlow.SERVERBOUND);
    }
}