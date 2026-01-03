package liedge.limacore.registry;

import liedge.limacore.lib.ModResources;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.common.BooleanAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

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

    private <T extends Attribute> DeferredHolder<Attribute, T> registerAttribute(String name, Attribute.Sentiment sentiment, boolean sync, Function<String, T> langKeyFactory)
    {
        return register(name, id ->
        {
            T attribute = langKeyFactory.apply(ModResources.prefixedIdLangKey("attribute", id));
            attribute.setSyncable(sync).setSentiment(sentiment);
            return attribute;
        });
    }

    public DeferredHolder<Attribute, BooleanAttribute> registerBool(String name, Attribute.Sentiment sentiment, boolean sync, boolean defaultValue)
    {
        return registerAttribute(name, sentiment, sync, descId -> new BooleanAttribute(descId, defaultValue));
    }

    public DeferredHolder<Attribute, RangedAttribute> registerRanged(String name, Attribute.Sentiment sentiment, boolean sync, double defaultValue, double min, double max)
    {
        return registerAttribute(name, sentiment, sync, descId -> new RangedAttribute(descId, defaultValue, min, max));
    }
}