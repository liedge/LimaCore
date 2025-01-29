package liedge.limacore.network.packet;

import io.netty.buffer.ByteBuf;
import liedge.limacore.LimaCore;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundBlockEntityDataRequestPacket(BlockPos blockPos) implements CustomPacketPayload
{
    static final Type<ServerboundBlockEntityDataRequestPacket> TYPE = LimaCore.RESOURCES.packetType("block_entity_data_request");
    static final StreamCodec<ByteBuf, ServerboundBlockEntityDataRequestPacket> STREAM_CODEC = BlockPos.STREAM_CODEC.map(ServerboundBlockEntityDataRequestPacket::new, ServerboundBlockEntityDataRequestPacket::blockPos);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
