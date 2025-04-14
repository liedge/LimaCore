package liedge.limacore.registry.game;

import liedge.limacore.LimaCore;
import liedge.limacore.lib.ModResources;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaCoreAttributes
{
    private LimaCoreAttributes() {}

    private static final DeferredRegister<Attribute> ATTRIBUTES = LimaCore.RESOURCES.deferredRegister(Registries.ATTRIBUTE);

    public static void register(IEventBus bus)
    {
        ATTRIBUTES.register(bus);
    }

    public static final DeferredHolder<Attribute, RangedAttribute> DAMAGE_MULTIPLIER = registerRanged("damage_multiplier", 1, 0, 2048, true, Attribute.Sentiment.POSITIVE);
    public static final DeferredHolder<Attribute, RangedAttribute> KNOCKBACK_MULTIPLIER = registerRanged("knockback_multiplier", 1, 0, 512, true, Attribute.Sentiment.NEGATIVE);

    private static DeferredHolder<Attribute, RangedAttribute> registerRanged(String name, double defaultValue, double min, double max, boolean sync, Attribute.Sentiment sentiment)
    {
        return ATTRIBUTES.register(name, id -> {
            RangedAttribute attribute = new RangedAttribute(ModResources.prefixedIdLangKey("generic.attribute", id), defaultValue, min, max);
            attribute.setSyncable(sync).setSentiment(sentiment);
            return attribute;
        });
    }
}