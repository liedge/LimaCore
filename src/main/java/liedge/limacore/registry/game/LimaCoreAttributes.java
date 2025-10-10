package liedge.limacore.registry.game;

import liedge.limacore.LimaCore;
import liedge.limacore.registry.LimaDeferredAttributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class LimaCoreAttributes
{
    private LimaCoreAttributes() {}

    private static final LimaDeferredAttributes ATTRIBUTES = LimaCore.RESOURCES.deferredAttributes();

    public static void register(IEventBus bus)
    {
        ATTRIBUTES.register(bus);
    }

    public static final DeferredHolder<Attribute, RangedAttribute> DAMAGE_MULTIPLIER = ATTRIBUTES.registerAttribute("damage_multiplier", 1, 0, 2048, true, Attribute.Sentiment.POSITIVE);
    public static final DeferredHolder<Attribute, RangedAttribute> KNOCKBACK_MULTIPLIER = ATTRIBUTES.registerAttribute("knockback_multiplier", 1, 0, 512, true, Attribute.Sentiment.NEGATIVE);
}