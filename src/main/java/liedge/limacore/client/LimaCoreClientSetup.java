package liedge.limacore.client;

import liedge.limacore.LimaCore;
import liedge.limacore.client.model.SimpleEmissiveGeometry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = LimaCore.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class LimaCoreClientSetup
{
    private LimaCoreClientSetup() {}

    @SubscribeEvent
    public static void registerGeometryLoaders(final ModelEvent.RegisterGeometryLoaders event)
    {
        SimpleEmissiveGeometry.LOADER.registerLoader(event);
    }
}