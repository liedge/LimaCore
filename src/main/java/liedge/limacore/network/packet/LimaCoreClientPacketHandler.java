package liedge.limacore.network.packet;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.network.IndexedStreamData;
import liedge.limacore.network.sync.DataWatcherHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
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
        if (Minecraft.getInstance().level != null)
        {
            ParticleOptions options = packet.data();
            Vec3 pos = packet.pos();
            Vec3 speed = packet.speed();

            Minecraft.getInstance().level.addParticle(options, options.getType().getOverrideLimiter(), pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
        }
    }
}