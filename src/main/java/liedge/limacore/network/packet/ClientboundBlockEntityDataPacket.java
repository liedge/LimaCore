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

public final class ClientboundBlockEntityDataPacket<T> extends DataWatcherPacketBase<T>
{
    static final PacketSpec<ClientboundBlockEntityDataPacket<?>> PACKET_SPEC = LimaCore.RESOURCES.packetSpec(PacketFlow.CLIENTBOUND, "block_entity_data", StreamCodec.of((net, pkt) -> pkt.encodePacket(net), ClientboundBlockEntityDataPacket::new));

    private final BlockPos pos;

    public ClientboundBlockEntityDataPacket(BlockPos pos, int index, NetworkSerializer<T> serializer, T data)
    {
        super(index, serializer, data);
        this.pos = pos;
    }

    ClientboundBlockEntityDataPacket(RegistryFriendlyByteBuf net)
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