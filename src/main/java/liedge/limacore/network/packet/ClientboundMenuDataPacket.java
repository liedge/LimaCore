package liedge.limacore.network.packet;

import liedge.limacore.LimaCore;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.network.sync.DataWatcherHolder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.Nullable;

public final class ClientboundMenuDataPacket<T> extends DataWatcherPacketBase<T>
{
    static final PacketSpec<ClientboundMenuDataPacket<?>> PACKET_SPEC = LimaCore.RESOURCES.packetSpec(PacketFlow.CLIENTBOUND, "menu_data", StreamCodec.of((net, pkt) -> pkt.encodePacket(net), ClientboundMenuDataPacket::new));

    private final int containerId;

    public ClientboundMenuDataPacket(int containerId, int index, NetworkSerializer<T> serializer, T data)
    {
        super(index, serializer, data);
        this.containerId = containerId;
    }

    ClientboundMenuDataPacket(RegistryFriendlyByteBuf net)
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