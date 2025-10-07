package liedge.limacore.client;

import com.mojang.logging.LogUtils;
import liedge.limacore.LimaCore;
import liedge.limacore.client.model.geometry.BlockLayerGeometry;
import liedge.limacore.client.model.geometry.ItemLayerGeometry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;
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
    }
}