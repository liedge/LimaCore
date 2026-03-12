package liedge.limacore.recipe;

import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.*;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public interface LimaRecipeCheck<I extends RecipeInput, R extends Recipe<I>> extends RecipeManager.CachedCheck<I, R>
{
    static <I extends RecipeInput, R extends Recipe<I>> LimaRecipeCheck<I, R> create(RecipeType<R> type)
    {
        return new SimpleCheck<>(type);
    }

    static <I extends RecipeInput, R extends Recipe<I>> LimaRecipeCheck<I, R> create(Supplier<? extends RecipeType<R>> typeSupplier)
    {
        return new SimpleCheck<>(typeSupplier.get());
    }

    RecipeType<R> getRecipeType();

    @Nullable ResourceKey<Recipe<?>> getLastUsedRecipeKey();

    void setLastUsedRecipeKey(@Nullable ResourceKey<Recipe<?>> lastUsedRecipeKey);

    @Override
    default Optional<RecipeHolder<R>> getRecipeFor(I input, ServerLevel level)
    {
        Optional<RecipeHolder<R>> lookup = level.recipeAccess().getRecipeFor(getRecipeType(), input, level, getLastUsedRecipeKey());
        lookup.ifPresent(o -> setLastUsedRecipeKey(o.id()));
        return lookup;
    }

    default Optional<RecipeHolder<R>> getLastUsedRecipe(ServerLevel level)
    {
        ResourceKey<Recipe<?>> key = getLastUsedRecipeKey();
        if (key == null) return Optional.empty();
        return LimaRegistryUtil.getRecipeByKey(level, key, getRecipeType());
    }

    default void setLastUsedRecipe(@Nullable RecipeHolder<R> lastUsedRecipe)
    {
        ResourceKey<Recipe<?>> key = lastUsedRecipe != null ? lastUsedRecipe.id() : null;
        setLastUsedRecipeKey(key);
    }

    @SuppressWarnings("NullableProblems")
    default LimaDataWatcher<Optional<ResourceKey<Recipe<?>>>> keepLastUsedSynced()
    {
        return AutomaticDataWatcher.keepNullableSynced(LimaCoreNetworkSerializers.OPTIONAL_RECIPE_KEY, this::getLastUsedRecipeKey, this::setLastUsedRecipeKey);
    }

    final class SimpleCheck<I extends RecipeInput, R extends Recipe<I>> implements LimaRecipeCheck<I, R>
    {
        private final RecipeType<R> recipeType;
        @Nullable
        private ResourceKey<Recipe<?>> lastUsedRecipeKey;

        private SimpleCheck(RecipeType<R> recipeType)
        {
            this.recipeType = recipeType;
        }

        @Override
        public RecipeType<R> getRecipeType()
        {
            return recipeType;
        }

        @Override
        public @Nullable ResourceKey<Recipe<?>> getLastUsedRecipeKey()
        {
            return lastUsedRecipeKey;
        }

        @Override
        public void setLastUsedRecipeKey(@Nullable ResourceKey<Recipe<?>> lastUsedRecipeKey)
        {
            this.lastUsedRecipeKey = lastUsedRecipeKey;
        }
    }
}