package liedge.limacore.data;

import liedge.limacore.lib.ModResources;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public interface BootstrapObjectBuilder<T>
{
    ResourceKey<T> key();

    T build();

    default void register(BootstrapContext<T> context)
    {
        context.register(key(), build());
    }

    default String identityTranslationKey()
    {
        return ModResources.registryPrefixedIdLangKey(key());
    }

    default String suffixTranslationKey(String suffix)
    {
        return ModResources.registryPrefixVariantIdLangKey(key(), suffix);
    }
}