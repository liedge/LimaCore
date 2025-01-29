package liedge.limacore.network.packet;

import liedge.limacore.inventory.menu.LimaMenu;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.util.LimaBlockUtil;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

final class LimaCoreServerPacketHandler
{
    private LimaCoreServerPacketHandler() {}

    public static void handleBlockDataRequestPacket(ServerboundBlockEntityDataRequestPacket packet, IPayloadContext context, ServerPlayer sender)
    {
        DataWatcherHolder holder = LimaBlockUtil.getSafeBlockEntity(sender.level(), packet.blockPos(), DataWatcherHolder.class);
        if (holder != null) holder.forceSyncDataWatchers();
    }

    public static <T> void handleCustomMenuButtonPacket(ServerboundCustomMenuButtonPacket<T> packet, IPayloadContext context, ServerPlayer sender)
    {
        if (sender.containerMenu instanceof LimaMenu<?> menu && menu.containerId == packet.containerId())
        {
            menu.handleCustomButtonData(sender, packet.buttonId(), packet.serializer(), packet.data());
        }
    }
}