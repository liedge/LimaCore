package liedge.limacore.util;

import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class LimaRegistryUtil
{
    private LimaRegistryUtil() {}

    public static <T> Stream<Map.Entry<ResourceKey<T>, T>> allNamespaceRegistryEntries(String modid, Registry<T> registry)
    {
        return registry.entrySet().stream().filter(e -> e.getKey().identifier().getNamespace().equals(modid));
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

    public static <T> Identifier getNonNullRegistryId(T object, Registry<T> registry)
    {
        Identifier id = registry.getKey(object);
        if (id == null || (registry instanceof DefaultedRegistry<T> defaultedRegistry && defaultedRegistry.getDefaultKey().equals(id)))
        {
            throw new NullPointerException("Object is not present in registry.");
        }
        else
        {
            return id;
        }
    }

    public static <T> Identifier getNonNullRegistryId(Holder<T> holder)
    {
        return getNonNullResourceKey(holder).identifier();
    }

    public static <T> T getNonNullRegistryValue(Identifier id, Registry<T> registry)
    {
        return Objects.requireNonNull(registry.getValue(id), () -> String.format("No value matching id '%s' found in registry [%s]", id, registry.key().identifier()));
    }

    public static <T> T getNonNullRegistryValue(ResourceKey<T> resourceKey, Registry<T> registry)
    {
        return Objects.requireNonNull(registry.getValue(resourceKey), () -> String.format("No value matching resource key '%s' found in registry [%s]", resourceKey.identifier(), registry.key().identifier()));
    }

    public static <T> Holder<T> getNonNullHolder(Identifier id, Registry<T> registry)
    {
        return registry.get(id).orElseThrow(() -> new NullPointerException(String.format("Missing holder for id '%s' in registry [%s]", id, registry.key().identifier())));
    }

    public static <T> Holder<T> getNonNullHolder(ResourceKey<T> resourceKey, Registry<T> registry)
    {
        return registry.get(resourceKey).orElseThrow(() -> new NullPointerException(String.format("Missing holder for resource key '%s' in registry [%s]", resourceKey.identifier(), registry.key().identifier())));
    }

    public static <T> Holder<T> getNonNullReferenceHolder(RegistryAccess registryAccess, ResourceKey<? extends Registry<T>> registryKey, Identifier id)
    {
        return registryAccess.lookupOrThrow(registryKey).get(id).orElseThrow(() -> new NullPointerException(String.format("Missing holder id '%s' in registry access for '%s'", id, registryKey.identifier())));
    }

    public static Identifier getItemId(Item item)
    {
        return getNonNullRegistryId(item, BuiltInRegistries.ITEM);
    }

    public static Identifier getBlockId(Block block)
    {
        return getNonNullRegistryId(block, BuiltInRegistries.BLOCK);
    }

    public static String getItemName(Item item)
    {
        return getItemId(item).getPath();
    }

    public static String getItemName(ItemStack stack)
    {
        return getItemName(stack.getItem());
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

    public static String getFluidName(FluidStack fluid)
    {
        return getFluidName(fluid.getFluidHolder());
    }

    public static String getFluidName(Fluid fluid)
    {
        return getNonNullRegistryId(fluid, BuiltInRegistries.FLUID).getPath();
    }

    public static String getFluidName(Holder<Fluid> holder)
    {
        return getNonNullRegistryId(holder).getPath();
    }

    //#region Recipe access
    @SuppressWarnings("unchecked")
    public static <T extends Recipe<?>> Optional<RecipeHolder<T>> getRecipeByKey(ServerLevel level, ResourceKey<Recipe<?>> recipeKey, RecipeType<T> recipeType)
    {
        return level.recipeAccess().byKey(recipeKey).filter(holder -> holder.value().getType().equals(recipeType)).map(h -> (RecipeHolder<T>) h);
    }

    public static <T extends Recipe<?>> Optional<RecipeHolder<T>> getRecipeByKey(ServerLevel level, ResourceKey<Recipe<?>> recipeKey, Supplier<? extends RecipeType<T>> typeSupplier)
    {
        return getRecipeByKey(level, recipeKey, typeSupplier.get());
    }
    //#endregion

    //#region Built in holder helpers
    @SuppressWarnings("deprecation")
    public static Holder<Item> builtInHolder(ItemLike itemLike)
    {
        return itemLike.asItem().builtInRegistryHolder();
    }

    @SuppressWarnings("deprecation")
    public static Holder<Fluid> builtInHolder(Fluid fluid)
    {
        return fluid.builtInRegistryHolder();
    }

    @SuppressWarnings("deprecation")
    public static Holder<BlockEntityType<?>> builtInHolder(BlockEntityType<?> type)
    {
        return type.builtInRegistryHolder();
    }

    @SuppressWarnings("deprecation")
    public static Holder<EntityType<?>> builtInHolder(EntityType<?> entityType)
    {
        return entityType.builtInRegistryHolder();
    }
    //#endregion

    public static <T> HolderSet<T> keyHolderSet(HolderGetter<T> holderGetter, ResourceKey<T> key)
    {
        return HolderSet.direct(holderGetter.getOrThrow(key));
    }

    @SafeVarargs
    public static <T> HolderSet<T> keyHolderSet(HolderGetter<T> holderGetter, ResourceKey<T>... keys)
    {
        return HolderSet.direct(holderGetter::getOrThrow, keys);
    }

    public static <T> HolderSet<T> mergeHolderSets(List<HolderSet<T>> holderSets)
    {
        return switch (holderSets.size())
        {
            case 0 -> HolderSet.empty();
            case 1 -> holderSets.getFirst();
            default -> new OrHolderSet<>(holderSets);
        };
    }
}