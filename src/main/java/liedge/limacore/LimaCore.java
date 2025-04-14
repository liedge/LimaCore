package liedge.limacore;

import com.mojang.logging.LogUtils;
import liedge.limacore.lib.ModResources;
import liedge.limacore.network.packet.LimaCorePackets;
import liedge.limacore.registry.*;
import liedge.limacore.registry.game.*;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
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
        LimaCoreAttributes.register(modBus);
        LimaCoreDataComponents.register(modBus);
        LimaCoreIngredientTypes.register(modBus);
        LimaCoreLootRegistries.register(modBus);
        LimaCoreNetworkSerializers.register(modBus);
        LimaCoreTriggerTypes.register(modBus);
        LimaCoreWorldGen.register(modBus);

        modBus.register(new CommonSetup());
    }

    private static class CommonSetup
    {
        @SubscribeEvent
        private void registerPayloadHandlers(final RegisterPayloadHandlersEvent event)
        {
            LimaCorePackets.registerPacketHandlers(event.registrar(MODID).versioned("1.0.0"));
        }

        @SubscribeEvent
        private void registerCustomRegistries(final NewRegistryEvent event)
        {
            event.register(LimaCoreRegistries.NETWORK_SERIALIZERS);
        }

        @SubscribeEvent
        private void modifyEntityAttributes(final EntityAttributeModificationEvent event)
        {
            for (EntityType<? extends LivingEntity> type : event.getTypes())
            {
                addAttributeToEntity(event, type, LimaCoreAttributes.DAMAGE_MULTIPLIER);
                addAttributeToEntity(event, type, LimaCoreAttributes.KNOCKBACK_MULTIPLIER);
            }
        }

        private void addAttributeToEntity(final EntityAttributeModificationEvent event, EntityType<? extends LivingEntity> type, Holder<Attribute> attribute)
        {
            if (!event.has(type, attribute)) event.add(type, attribute);
        }
    }
}