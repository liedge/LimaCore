package liedge.limacore.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.input.RecipeItemInput;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class LimaClientRecipes
{
    private final LoadingCache<ResourceKey<Recipe<?>>, List<CachedDisplay>> ingredientDisplayCache = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.ofSeconds(15L))
            .build(new CacheLoader<>()
            {
                @Override
                public List<CachedDisplay> load(ResourceKey<Recipe<?>> key)
                {
                    RecipeHolder<?> holder = recipes.byKey(key);
                    if (holder == null || !(holder.value() instanceof LimaCustomRecipe<?> limaRecipe)) return List.of();

                    ObjectList<CachedDisplay> list = new ObjectArrayList<>();
                    List<RecipeItemInput> inputs = limaRecipe.getItemInputs();
                    ContextMap contextMap = SlotDisplayContext.fromLevel(Objects.requireNonNull(Minecraft.getInstance().level, "Client level not loaded"));

                    for (RecipeItemInput input : inputs)
                    {
                        List<ItemStack> stacks = input.ingredient().display().resolve(contextMap, input.displayResolver()).toList();
                        list.add(new CachedDisplay(input.consumeChance(), stacks));
                    }

                    return ObjectLists.unmodifiable(list);
                }
            });

    private RecipeMap recipes = RecipeMap.EMPTY;

    public LimaClientRecipes() { }

    public void register(IEventBus bus)
    {
        bus.addListener(RecipesReceivedEvent.class, this::onLoad);
        bus.addListener(ClientPlayerNetworkEvent.LoggingOut.class, _ -> onRemoved());
    }

    public RecipeMap getRecipes()
    {
        return recipes;
    }

    @SuppressWarnings("unchecked")
    public <T extends Recipe<?>> @Nullable RecipeHolder<T> byKey(RecipeType<T> recipeType, @Nullable ResourceKey<Recipe<?>> key)
    {
        if (key == null) return null;

        RecipeHolder<?> holder = recipes.byKey(key);
        if (holder != null && holder.value().getType() == recipeType)
        {
            return (RecipeHolder<T>) holder;
        }
        else
        {
            return null;
        }
    }

    public <T extends Recipe<?>> @Nullable RecipeHolder<T> byKey(Supplier<? extends RecipeType<T>> typeSupplier, @Nullable ResourceKey<Recipe<?>> key)
    {
        return byKey(typeSupplier.get(), key);
    }

    public <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> byType(Supplier<? extends RecipeType<T>> typeSupplier)
    {
        return recipes.byType(typeSupplier.get());
    }

    public <T extends LimaCustomRecipe<?>> List<CachedDisplay> getDisplaysForTooltips(RecipeType<T> type, @Nullable ResourceKey<Recipe<?>> key)
    {
        if (key == null) return List.of();
        return ingredientDisplayCache.getUnchecked(key);
    }

    public <T extends LimaCustomRecipe<?>> List<CachedDisplay> getDisplaysForTooltips(Supplier<? extends RecipeType<T>> typeSupplier, @Nullable ResourceKey<Recipe<?>> key)
    {
        return getDisplaysForTooltips(typeSupplier.get(), key);
    }

    protected void onLoad(final RecipesReceivedEvent event)
    {
        ingredientDisplayCache.invalidateAll();
        this.recipes = event.getRecipeMap();
        LimaCoreClient.CLIENT_LOGGER.info("Received client-side recipe data for {} types", event.getRecipeTypes().size());
    }

    protected void onRemoved()
    {
        recipes = RecipeMap.EMPTY;
        ingredientDisplayCache.invalidateAll();
        LimaCoreClient.CLIENT_LOGGER.info("Unloading client-side recipe data and display caches");
    }

    public record CachedDisplay(float consumeChance, List<ItemStack> stacks) { }
}