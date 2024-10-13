package liedge.limacore.network.packet;

import liedge.limacore.LimaCore;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.network.sync.DataWatcherHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.Nullable;

public final class ClientboundBlockEntityDataWatcherPacket<T> extends ClientboundDataWatcherPacket<T>
{
    static final PacketSpec<ClientboundBlockEntityDataWatcherPacket<?>> PACKET_SPEC = LimaCore.RESOURCES.packetSpec(PacketFlow.CLIENTBOUND, "block_entity_data", StreamCodec.of((net, pkt) -> pkt.encodePacket(net), ClientboundBlockEntityDataWatcherPacket::new));

    private final BlockPos pos;

    public ClientboundBlockEntityDataWatcherPacket(BlockPos pos, int index, NetworkSerializer<T> serializer, T data)
    {
        super(index, serializer, data);
        this.pos = pos;
    }

    ClientboundBlockEntityDataWatcherPacket(RegistryFriendlyByteBuf net)
    {
        super(net);
        this.pos = net.readBlockPos();
    }

    @Override
    void encodeWatcherContext(RegistryFriendlyByteBuf net)
    {
        net.writeBlockPos(pos);
    }

    @Override
    @Nullable DataWatcherHolder watcherContext()
    {
        return LimaCoreClientUtil.getClientSafeBlockEntity(pos, DataWatcherHolder.class);
    }

    @Override
    public PacketSpec<?> getPacketSpec()
    {
        return PACKET_SPEC;
    }
}