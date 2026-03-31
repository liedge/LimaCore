package liedge.limacore.client;

import com.mojang.logging.LogUtils;
import liedge.limacore.LimaCore;
import liedge.limacore.client.model.EmissiveUnbakedModel;
import liedge.limacore.client.particle.CustomGeometryParticleGroup;
import liedge.limacore.client.renderer.LimaCoreRenderPipelines;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterParticleGroupsEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(value = LimaCore.MODID, dist = Dist.CLIENT)
public class LimaCoreClient
{
    private static final LimaClientRecipes CLIENT_RECIPES = new LimaClientRecipes();

    public static final Logger CLIENT_LOGGER = LogUtils.getLogger();

    public static LimaClientRecipes getClientRecipes()
    {
        return CLIENT_RECIPES;
    }

    public LimaCoreClient(IEventBus modBus, ModContainer modContainer)
    {
        modBus.register(new ClientSetup());

        CLIENT_RECIPES.register(NeoForge.EVENT_BUS);
    }

    private static class ClientSetup
    {
        @SubscribeEvent
        public void registerParticleGroups(final RegisterParticleGroupsEvent event)
        {
            event.register(CustomGeometryParticleGroup.CUSTOM_GEOMETRY_PARTICLE, CustomGeometryParticleGroup::new);
        }

        @SubscribeEvent
        private void registerModeLoaders(final ModelEvent.RegisterLoaders event)
        {
            event.register(EmissiveUnbakedModel.LOADER_ID, EmissiveUnbakedModel.LOADER);
        }

        @SubscribeEvent
        private void registerRenderPipelines(final RegisterRenderPipelinesEvent event)
        {
            event.registerPipeline(LimaCoreRenderPipelines.ENTITY_CUTOUT_EMISSIVE);
            event.registerPipeline(LimaCoreRenderPipelines.ENTITY_CUTOUT_CULL_EMISSIVE);
            event.registerPipeline(LimaCoreRenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE);
        }
    }
}