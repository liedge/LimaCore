package liedge.limacore.network.packet;

import io.netty.buffer.ByteBuf;
import liedge.limacore.LimaCore;
import liedge.limacore.network.ServerboundPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundBlockEntityDataRequestPacket(BlockPos blockPos) implements ServerboundPayload
{
    public static final Type<ServerboundBlockEntityDataRequestPacket> TYPE = LimaCore.RESOURCES.packetType("block_entity_data_request");
    public static final StreamCodec<ByteBuf, ServerboundBlockEntityDataRequestPacket> STREAM_CODEC = BlockPos.STREAM_CODEC.map(ServerboundBlockEntityDataRequestPacket::new, ServerboundBlockEntityDataRequestPacket::blockPos);

    @Override
    public void handleServer(ServerPlayer sender, IPayloadContext context)
    {
        LimaCoreServerPacketHandler.handleBlockDataRequestPacket(this, sender);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
