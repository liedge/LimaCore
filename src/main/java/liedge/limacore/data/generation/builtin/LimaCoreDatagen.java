package liedge.limacore.data.generation.builtin;

import liedge.limacore.LimaCore;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = LimaCore.MODID, bus = EventBusSubscriber.Bus.MOD)
final class LimaCoreDatagen
{
    private LimaCoreDatagen() {}

    @SubscribeEvent
    public static void runDataGeneration(final GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        // Client assets
        generator.addProvider(event.includeClient(), new LanguageGen(output));
    }
}