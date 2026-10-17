package liedge.limacore.network.packet;

import liedge.limacore.client.util.LimaCoreClientUtil;
import liedge.limacore.network.IndexedStreamData;
import liedge.limacore.network.sync.DataWatcherHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

final class LimaCoreClientPacketHandler
{
    private LimaCoreClientPacketHandler() {}

    private static void handleDataWatcherPacket(@Nullable DataWatcherHolder holder, List<IndexedStreamData<?>> streamData)
    {
        if (holder != null) holder.receiveDataWatcherPacket(streamData);
    }

    static void handleMenuDataWatcherPacket(ClientboundMenuDataWatcherPacket packet)
    {
        handleDataWatcherPacket(LimaCoreClientUtil.getClientPlayerMenu(packet.containerId(), DataWatcherHolder.class), packet.streamData());
    }

    static void handleBlockDataWatcherPacket(ClientboundBlockEntityDataWatcherPacket packet)
    {
        handleDataWatcherPacket(LimaCoreClientUtil.getClientSafeBlockEntity(packet.blockPos(), DataWatcherHolder.class), packet.streamData());
    }

    static void handleParticlePacket(ClientboundParticlePacket packet)
    {
        Level level = Minecraft.getInstance().level;
        if (level != null)
        {
            Vec3 pos = packet.pos();
            Vec3 aux = packet.aux();
            level.addParticle(packet.data(), packet.overrideLimiter(), packet.alwaysShow(), pos.x, pos.y, pos.z, aux.x, aux.y, aux.z);
        }
    }
}