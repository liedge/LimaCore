package liedge.limacore.network.packet;

import liedge.limacore.LimaCore;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.network.sync.DataWatcherHolder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.Nullable;

public final class ClientboundMenuDataWatcherPacket<T> extends ClientboundDataWatcherPacket<T>
{
    static final PacketSpec<ClientboundMenuDataWatcherPacket<?>> PACKET_SPEC = LimaCore.RESOURCES.packetSpec(PacketFlow.CLIENTBOUND, "menu_data", StreamCodec.of((net, pkt) -> pkt.encodePacket(net), ClientboundMenuDataWatcherPacket::new));

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

    @Override
    void encodeWatcherContext(RegistryFriendlyByteBuf net)
    {
        net.writeVarInt(containerId);
    }

    @Override
    @Nullable DataWatcherHolder watcherContext()
    {
        return LimaCoreClientUtil.getClientPlayerMenu(containerId, DataWatcherHolder.class);
    }

    @Override
    public PacketSpec<?> getPacketSpec()
    {
        return PACKET_SPEC;
    }
}