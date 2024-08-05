package liedge.limacore.network.packet;

import liedge.limacore.LimaCore;
import liedge.limacore.inventory.menu.LimaMenu;
import liedge.limacore.network.NetworkSerializer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static liedge.limacore.network.NetworkSerializer.REGISTRY_STREAM_CODEC;

public final class ServerboundCustomMenuButtonPacket<T> implements LimaPlayPacket.ServerboundOnly
{
    static final PacketSpec<ServerboundCustomMenuButtonPacket<?>> PACKET_SPEC = LimaCore.RESOURCES.packetSpec(PacketFlow.SERVERBOUND, "custom_menu_button", StreamCodec.of((net, o) -> o.encodePacket(net), ServerboundCustomMenuButtonPacket::new));

    private final int containerId;
    private final int buttonId;
    private final NetworkSerializer<T> serializer;
    private final T data;

    public ServerboundCustomMenuButtonPacket(int containerId, int buttonId, NetworkSerializer<T> serializer, T data)
    {
        this.containerId = containerId;
        this.buttonId = buttonId;
        this.serializer = serializer;
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    private ServerboundCustomMenuButtonPacket(RegistryFriendlyByteBuf net)
    {
        this.containerId = net.readVarInt();
        this.buttonId = net.readVarInt();
        this.serializer = (NetworkSerializer<T>) REGISTRY_STREAM_CODEC.decode(net);
        this.data = serializer.streamCodec().decode(net);
    }

    private void encodePacket(RegistryFriendlyByteBuf net)
    {
        net.writeVarInt(containerId);
        net.writeVarInt(buttonId);
        REGISTRY_STREAM_CODEC.encode(net, serializer);
        serializer.streamCodec().encode(net, data);
    }

    @Override
    public void onReceivedByServer(IPayloadContext context, ServerPlayer sender)
    {
        if (sender.containerMenu instanceof LimaMenu<?> menu && menu.containerId == containerId)
        {
            menu.handleCustomButtonData(sender, buttonId, serializer, data);
        }
    }

    @Override
    public PacketSpec<?> getPacketSpec()
    {
        return PACKET_SPEC;
    }
}