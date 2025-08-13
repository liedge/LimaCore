package liedge.limacore.client;

import liedge.limacore.LimaCore;
import liedge.limacore.client.model.geometry.BlockLayerGeometry;
import liedge.limacore.client.model.geometry.ItemLayerGeometry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = LimaCore.MODID, value = Dist.CLIENT)
public final class LimaCoreClientSetup
{
    private LimaCoreClientSetup() {}

    @SubscribeEvent
    public static void registerGeometryLoaders(final ModelEvent.RegisterGeometryLoaders event)
    {
        ItemLayerGeometry.LOADER.registerLoader(event);
        BlockLayerGeometry.LOADER.registerLoader(event);
    }
}