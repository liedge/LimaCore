package liedge.limacore.network.packet;

import liedge.limacore.LimaCore;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.network.ServerboundPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static liedge.limacore.network.NetworkSerializer.REGISTRY_STREAM_CODEC;

public record ServerboundCustomMenuButtonPacket<T>(int containerId, int buttonId, NetworkSerializer<T> serializer, T data) implements ServerboundPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundCustomMenuButtonPacket<?>> STREAM_CODEC = StreamCodec.of((net, msg) -> msg.encodePacket(net), ServerboundCustomMenuButtonPacket::decode);
    public static final Type<ServerboundCustomMenuButtonPacket<?>> TYPE = LimaCore.RESOURCES.packetType("custom_menu_button");

    @SuppressWarnings("unchecked")
    private static <T> ServerboundCustomMenuButtonPacket<T> decode(RegistryFriendlyByteBuf net)
    {
        int containerId = net.readVarInt();
        int buttonId = net.readVarInt();
        NetworkSerializer<T> serializer = (NetworkSerializer<T>) REGISTRY_STREAM_CODEC.decode(net);
        T data = serializer.decode(net);

        return new ServerboundCustomMenuButtonPacket<>(containerId, buttonId, serializer, data);
    }

    private void encodePacket(RegistryFriendlyByteBuf net)
    {
        net.writeVarInt(containerId);
        net.writeVarInt(buttonId);
        REGISTRY_STREAM_CODEC.encode(net, serializer);
        serializer.encode(net, data);
    }

    @Override
    public void handleServer(ServerPlayer sender, IPayloadContext context)
    {
        LimaCoreServerPacketHandler.handleCustomMenuButtonPacket(this, sender);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}