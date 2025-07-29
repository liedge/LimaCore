package liedge.limacore.util;

import net.minecraft.core.*;
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

    public static <T> ResourceKey<T> getNonNullResourceKey(Holder<T> holder)
    {
        return holder.unwrapKey().orElseThrow(() -> new RuntimeException("Holder is missing resource key."));
    }

    public static <T> ResourceLocation getNonNullRegistryId(T object, Registry<T> registry)
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

    public static <T> ResourceLocation getNonNullRegistryId(Holder<T> holder)
    {
        return getNonNullResourceKey(holder).location();
    }

    public static <T> T getNonNullRegistryValue(ResourceLocation id, Registry<T> registry)
    {
        return Objects.requireNonNull(registry.get(id), () -> String.format("No value matching id '%s' found in registry [%s]", id, registry.key().location()));
    }

    public static <T> T getNonNullRegistryValue(ResourceKey<T> resourceKey, Registry<T> registry)
    {
        return Objects.requireNonNull(registry.get(resourceKey), () -> String.format("No value matching resource key '%s' found in registry [%s]", resourceKey.location(), registry.key().location()));
    }

    public static <T> Holder<T> getNonNullHolder(ResourceLocation id, Registry<T> registry)
    {
        return registry.getHolder(id).orElseThrow(() -> new NullPointerException(String.format("Missing holder for id '%s' in registry [%s]", id, registry.key().location())));
    }

    public static <T> Holder<T> getNonNullHolder(ResourceKey<T> resourceKey, Registry<T> registry)
    {
        return registry.getHolder(resourceKey).orElseThrow(() -> new NullPointerException(String.format("Missing holder for resource key '%s' in registry [%s]", resourceKey.location(), registry.key().location())));
    }

    public static <T> Holder<T> getNonNullReferenceHolder(RegistryAccess registryAccess, ResourceKey<? extends Registry<T>> registryKey, ResourceLocation id)
    {
        return registryAccess.registryOrThrow(registryKey).getHolder(id).orElseThrow(() -> new NullPointerException(String.format("Missing holder id '%s' in registry access for '%s'", id, registryKey.location())));
    }

    public static ResourceLocation getItemId(Item item)
    {
        return getNonNullRegistryId(item, BuiltInRegistries.ITEM);
    }

    public static ResourceLocation getBlockId(Block block)
    {
        return getNonNullRegistryId(block, BuiltInRegistries.BLOCK);
    }

    public static String getItemName(Item item)
    {
        return getItemId(item).getPath();
    }

    public static String getItemName(Holder<Item> holder)
    {
        return getNonNullRegistryId(holder).getPath();
    }

    public static String getBlockName(Block block)
    {
        return getBlockId(block).getPath();
    }

    public static String getBlockName(Holder<Block> holder)
    {
        return getNonNullRegistryId(holder).getPath();
    }

    public static <T> HolderSet<T> keyHolderSet(HolderGetter<T> holderGetter, ResourceKey<T> key)
    {
        return HolderSet.direct(holderGetter.getOrThrow(key));
    }

    @SafeVarargs
    public static <T> HolderSet<T> keyHolderSet(HolderGetter<T> holderGetter, ResourceKey<T>... keys)
    {
        return HolderSet.direct(holderGetter::getOrThrow, keys);
    }
}