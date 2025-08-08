package liedge.limacore.network.packet;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.network.sync.DataWatcherHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

final class LimaCoreClientPacketHandler
{
    private LimaCoreClientPacketHandler() {}

    private static void handleDataWatcherPacket(@Nullable DataWatcherHolder holder, int index, Object data)
    {
        if (holder != null) holder.receiveDataPacket(index, data);
    }

    static <T> void handleMenuDataWatcherPacket(ClientboundMenuDataWatcherPacket<T> packet)
    {
        handleDataWatcherPacket(LimaCoreClientUtil.getClientPlayerMenu(packet.getContainerId(), DataWatcherHolder.class), packet.getIndex(), packet.getData());
    }

    static <T> void handleBlockDataWatcherPacket(ClientboundBlockEntityDataWatcherPacket<T> packet)
    {
        handleDataWatcherPacket(LimaCoreClientUtil.getClientSafeBlockEntity(packet.getPos(), DataWatcherHolder.class), packet.getIndex(), packet.getData());
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