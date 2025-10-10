package liedge.limacore.registry;

import liedge.limacore.lib.ModResources;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaDeferredAttributes extends DeferredRegister<Attribute>
{
    public static LimaDeferredAttributes create(String namespace)
    {
        return new LimaDeferredAttributes(namespace);
    }

    private LimaDeferredAttributes(String namespace)
    {
        super(Registries.ATTRIBUTE, namespace);
    }

    public DeferredHolder<Attribute, RangedAttribute> registerAttribute(String name, double defaultValue, double min, double max, boolean sync, Attribute.Sentiment sentiment)
    {
        return register(name, id ->
        {
            RangedAttribute attribute = new RangedAttribute(ModResources.prefixedIdLangKey("attribute", id), defaultValue, min, max);
            attribute.setSyncable(sync).setSentiment(sentiment);
            return attribute;
        });
    }
}