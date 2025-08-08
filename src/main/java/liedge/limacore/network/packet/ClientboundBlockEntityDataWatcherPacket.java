package liedge.limacore.network.packet;

import liedge.limacore.LimaCore;
import liedge.limacore.network.NetworkSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientboundBlockEntityDataWatcherPacket<T> extends ClientboundDataWatcherPacket<T>
{
    public static final Type<ClientboundBlockEntityDataWatcherPacket<?>> TYPE = LimaCore.RESOURCES.packetType("block_entity_data");
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundBlockEntityDataWatcherPacket<?>> STREAM_CODEC = StreamCodec.of((net, pkt) -> pkt.encodePacket(net), ClientboundBlockEntityDataWatcherPacket::new);

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

    public BlockPos getPos()
    {
        return pos;
    }

    @Override
    void encodeWatcherContext(RegistryFriendlyByteBuf net)
    {
        net.writeBlockPos(pos);
    }

    @Override
    public void handleClient(IPayloadContext context)
    {
        LimaCoreClientPacketHandler.handleBlockDataWatcherPacket(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}