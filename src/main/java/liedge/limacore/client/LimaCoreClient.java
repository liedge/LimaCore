package liedge.limacore.client;

import com.mojang.logging.LogUtils;
import liedge.limacore.LimaCore;
import liedge.limacore.client.particle.CustomGeometryParticleGroup;
import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterNamedRenderTypesEvent;
import net.neoforged.neoforge.client.event.RegisterParticleGroupsEvent;
import org.slf4j.Logger;

@Mod(value = LimaCore.MODID, dist = Dist.CLIENT)
public class LimaCoreClient
{
    public static final Logger CLIENT_LOGGER = LogUtils.getLogger();

    public LimaCoreClient(IEventBus modBus, ModContainer modContainer)
    {
        modBus.register(new ClientSetup());
    }

    private static class ClientSetup
    {
        @SubscribeEvent
        public void registerParticleGroups(final RegisterParticleGroupsEvent event)
        {
            event.register(CustomGeometryParticleGroup.CUSTOM_GEOMETRY_PARTICLE, CustomGeometryParticleGroup::new);
        }

        @SubscribeEvent
        public void registerNamedRenderTypes(final RegisterNamedRenderTypesEvent event)
        {
            event.register(LimaCoreRenderTypes.ITEM_CUTOUT_UNLIT_ID, ChunkSectionLayer.CUTOUT, LimaCoreRenderTypes::entityCutoutUnlit);
        }
    }
}