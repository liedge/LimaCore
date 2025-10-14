package liedge.limacore.network.packet;

import liedge.limacore.LimaCore;
import liedge.limacore.network.ClientboundPayload;
import liedge.limacore.network.IndexedStreamData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record ClientboundBlockEntityDataWatcherPacket(List<IndexedStreamData<?>> streamData, BlockPos blockPos) implements ClientboundPayload
{
    public static final Type<ClientboundBlockEntityDataWatcherPacket> TYPE = LimaCore.RESOURCES.packetType("block_entity_data");
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundBlockEntityDataWatcherPacket> STREAM_CODEC = StreamCodec.composite(
            IndexedStreamData.LIST_STREAM_CODEC, ClientboundBlockEntityDataWatcherPacket::streamData,
            BlockPos.STREAM_CODEC, ClientboundBlockEntityDataWatcherPacket::blockPos,
            ClientboundBlockEntityDataWatcherPacket::new);

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