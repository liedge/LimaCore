package liedge.limacore;

import com.mojang.logging.LogUtils;
import liedge.limacore.lib.ModResources;
import liedge.limacore.network.packet.LimaCorePackets;
import liedge.limacore.registry.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.slf4j.Logger;

@Mod(LimaCore.MODID)
public class LimaCore
{
    public static final String MODID = "limacore";
    public static final ModResources RESOURCES = new ModResources(MODID);
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
        modBus.addListener(this::registerCustomRegistries);
    }

    // Mod events
    private void registerPayloadHandlers(final RegisterPayloadHandlersEvent event)
    {
        LimaCorePackets.registerPacketHandlers(event.registrar(MODID).versioned("1.0.0"));
    }

    private void registerCustomRegistries(final NewRegistryEvent event)
    {
        event.register(LimaCoreRegistries.NETWORK_SERIALIZERS);
    }
}