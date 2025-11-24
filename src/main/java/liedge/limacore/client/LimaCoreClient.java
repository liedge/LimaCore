package liedge.limacore.client;

import com.mojang.logging.LogUtils;
import liedge.limacore.LimaCore;
import liedge.limacore.client.model.geometry.BlockLayerGeometry;
import liedge.limacore.client.model.geometry.ItemLayerGeometry;
import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterNamedRenderTypesEvent;
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
        public void registerGeometryLoaders(final ModelEvent.RegisterGeometryLoaders event)
        {
            ItemLayerGeometry.LOADER.registerLoader(event);
            BlockLayerGeometry.LOADER.registerLoader(event);
            CLIENT_LOGGER.info("Registered LimaCore geometry loaders.");
        }

        @SubscribeEvent
        public void registerNamedRenderTypes(final RegisterNamedRenderTypesEvent event)
        {
            event.register(LimaCoreRenderTypes.EMISSIVE_SOLID_ITEM_NAME, RenderType.solid(), LimaCoreRenderTypes.ITEM_POS_TEX_COLOR_SOLID);
        }
    }
}