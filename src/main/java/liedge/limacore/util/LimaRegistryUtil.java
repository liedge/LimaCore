package liedge.limacore.util;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public final class LimaRegistryUtil
{
    private LimaRegistryUtil() {}

    public static <T> Stream<Map.Entry<ResourceKey<T>, T>> allNamespaceRegistryEntries(String modid, Registry<T> registry)
    {
        return registry.entrySet().stream().filter(e -> e.getKey().location().getNamespace().equals(modid));
    }

    public static <T> Stream<T> allNamespaceRegistryValues(String modid, Registry<T> registry)
    {
        return allNamespaceRegistryEntries(modid, registry).map(Map.Entry::getValue);
    }

    public static <T> ResourceKey<T> getNonNullResourceKey(T object, Registry<T> registry)
    {
        return registry.getResourceKey(object).orElseThrow(() -> new NullPointerException("Object is not present in registry."));
    }

    public static <T> ResourceLocation getNonNullRegistryKey(T object, Registry<T> registry)
    {
        ResourceLocation id = registry.getKey(object);
        if (id == null || (registry instanceof DefaultedRegistry<T> defaultedRegistry && defaultedRegistry.getDefaultKey().equals(id)))
        {
            throw new NullPointerException("Object is not present in registry.");
        }
        else
        {
            return id;
        }
    }

    public static <T> T getNonNullRegistryValue(ResourceLocation key, Registry<T> registry)
    {
        return Objects.requireNonNull(registry.get(key), "No value matching key '" + key + "' found in registry [" + registry.key().location() + "'");
    }

    public static <T> T getNonNullRegistryValue(ResourceKey<T> resourceKey, Registry<T> registry)
    {
        return Objects.requireNonNull(registry.get(resourceKey), "No value matching resource key '" + resourceKey.location() + "' found in registry [" + registry.key().location() + "'");
    }

    public static ResourceLocation getItemId(Item item)
    {
        return getNonNullRegistryKey(item, BuiltInRegistries.ITEM);
    }

    public static ResourceLocation getBlockId(Block block)
    {
        return getNonNullRegistryKey(block, BuiltInRegistries.BLOCK);
    }

    public static String getItemName(Item item)
    {
        return getItemId(item).getPath();
    }

    public static String getBlockName(Block block)
    {
        return getBlockId(block).getPath();
    }
}