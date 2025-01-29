package liedge.limacore.network.packet;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.network.sync.DataWatcherHolder;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

final class LimaCoreClientPacketHandler
{
    private LimaCoreClientPacketHandler() {}

    private static void handleDataWatcherPacket(@Nullable DataWatcherHolder holder, int index, Object data)
    {
        if (holder != null) holder.receiveDataPacket(index, data);
    }

    public static <T> void handleMenuDataWatcherPacket(ClientboundMenuDataWatcherPacket<T> packet, IPayloadContext context)
    {
        handleDataWatcherPacket(LimaCoreClientUtil.getClientPlayerMenu(packet.getContainerId(), DataWatcherHolder.class), packet.getIndex(), packet.getData());
    }

    public static <T> void handleBlockDataWatcherPacket(ClientboundBlockEntityDataWatcherPacket<T> packet, IPayloadContext context)
    {
        handleDataWatcherPacket(LimaCoreClientUtil.getClientSafeBlockEntity(packet.getPos(), DataWatcherHolder.class), packet.getIndex(), packet.getData());
    }
}