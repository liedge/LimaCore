package liedge.limacore.network.packet;

import liedge.limacore.menu.LimaMenu;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.util.LimaBlockUtil;
import net.minecraft.server.level.ServerPlayer;

public final class LimaCoreServerPacketHandler
{
    private LimaCoreServerPacketHandler() {}

    static void handleBlockDataRequestPacket(ServerboundBlockEntityDataRequestPacket packet, ServerPlayer sender)
    {
        DataWatcherHolder holder = LimaBlockUtil.getSafeBlockEntity(sender.level(), packet.blockPos(), DataWatcherHolder.class);
        if (holder != null) holder.forceSyncDataWatchers();
    }

    static <T> void handleCustomMenuButtonPacket(ServerboundCustomMenuButtonPacket<T> packet, ServerPlayer sender)
    {
        if (sender.containerMenu instanceof LimaMenu<?> menu && menu.containerId == packet.containerId())
        {
            menu.handleCustomButtonData(sender, packet.buttonId(), packet.serializer(), packet.data());
        }
    }

    static void handleFluidSlotClick(ServerboundFluidSlotClickPacket packet, ServerPlayer sender)
    {
        if (sender.containerMenu instanceof LimaMenu<?> menu && menu.containerId == packet.containerId())
        {
            menu.fluidSlotClicked(sender, packet.slotIndex(), packet.action());
        }
    }
}