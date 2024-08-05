package liedge.limacore.network.packet;

import liedge.limacore.LimaCore;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.util.LimaCoreUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundBlockEntityDataRequestPacket(BlockPos blockPos) implements LimaPlayPacket.ServerboundOnly
{
    static final PacketSpec<ServerboundBlockEntityDataRequestPacket> PACKET_SPEC = LimaCore.RESOURCES.packetSpec(PacketFlow.SERVERBOUND, "block_entity_data_request", BlockPos.STREAM_CODEC.map(ServerboundBlockEntityDataRequestPacket::new, ServerboundBlockEntityDataRequestPacket::blockPos));

    @Override
    public void onReceivedByServer(IPayloadContext context, ServerPlayer sender)
    {
        DataWatcherHolder holder = LimaCoreUtil.getSafeBlockEntity(sender.level(), blockPos, DataWatcherHolder.class);
        if (holder != null)
        {
            holder.tickDataWatchers(true);
        }
    }

    @Override
    public PacketSpec<?> getPacketSpec()
    {
        return PACKET_SPEC;
    }
}
