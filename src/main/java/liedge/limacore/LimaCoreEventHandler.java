package liedge.limacore;

import liedge.limacore.registry.LimaCoreTriggerTypes;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = LimaCore.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class LimaCoreEventHandler
{
    private LimaCoreEventHandler() {}

    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer serverPlayer)
        {
            LimaCoreTriggerTypes.PLAYER_LOGGED_IN.get().trigger(serverPlayer);
        }
    }
}