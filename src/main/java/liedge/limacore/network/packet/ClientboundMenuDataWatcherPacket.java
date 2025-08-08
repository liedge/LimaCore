package liedge.limacore.network.packet;

import liedge.limacore.LimaCore;
import liedge.limacore.network.NetworkSerializer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientboundMenuDataWatcherPacket<T> extends ClientboundDataWatcherPacket<T>
{
    public static final Type<ClientboundMenuDataWatcherPacket<?>> TYPE = LimaCore.RESOURCES.packetType("menu_data");
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundMenuDataWatcherPacket<?>> STREAM_CODEC = StreamCodec.of((net, pkt) -> pkt.encodePacket(net), ClientboundMenuDataWatcherPacket::new);

    private final int containerId;

    public ClientboundMenuDataWatcherPacket(int containerId, int index, NetworkSerializer<T> serializer, T data)
    {
        super(index, serializer, data);
        this.containerId = containerId;
    }

    private ClientboundMenuDataWatcherPacket(RegistryFriendlyByteBuf net)
    {
        super(net);
        this.containerId = net.readVarInt();
    }

    public int getContainerId()
    {
        return containerId;
    }

    @Override
    void encodeWatcherContext(RegistryFriendlyByteBuf net)
    {
        net.writeVarInt(containerId);
    }

    @Override
    public void handleClient(IPayloadContext context)
    {
        LimaCoreClientPacketHandler.handleMenuDataWatcherPacket(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}