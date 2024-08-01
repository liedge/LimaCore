package liedge.limacore;

import com.mojang.logging.LogUtils;
import liedge.limacore.lib.ModResources;
import liedge.limacore.network.packet.LimaCorePackets;
import liedge.limacore.registry.*;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;

@Mod(LimaCore.MODID)
public class LimaCore
{
    public static final String MODID = "limacore";
    public static final ModResources RESOURCES = new ModResources("limacore");
    public static final Logger LOGGER = LogUtils.getLogger();

    public LimaCore(IEventBus modBus, ModContainer modContainer)
    {
        LimaCoreDataComponents.initRegister(modBus);
        LimaCoreIngredientTypes.init(modBus);
        LimaCoreLootRegistries.initRegister(modBus);
        LimaCoreNetworkSerializers.initRegister(modBus);
        LimaCoreTriggerTypes.init(modBus);
        LimaCoreWorldGen.initRegister(modBus);

        modBus.addListener(this::registerPayloadHandlers);

        NeoForge.EVENT_BUS.addListener(this::onPlayerLogin);
    }

    // Mod events
    private void registerPayloadHandlers(final RegisterPayloadHandlersEvent event)
    {
        LimaCorePackets.registerPacketHandlers(event.registrar(MODID).versioned("1.0.0"));
    }

    // Game events
    private void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer serverPlayer)
        {
            LimaCoreTriggerTypes.PLAYER_LOGGED_IN.get().trigger(serverPlayer);
        }
    }
}