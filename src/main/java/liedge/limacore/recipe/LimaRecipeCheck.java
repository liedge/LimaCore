package liedge.limacore.recipe;

import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaRecipesUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    ResourceLocation getLastUsedRecipeId();

    void setLastUsedRecipeId(@Nullable ResourceLocation lastUsedRecipeId);

    @Override
    default Optional<RecipeHolder<R>> getRecipeFor(I input, Level level)
    {
        Optional<RecipeHolder<R>> lookup = level.getRecipeManager().getRecipeFor(getRecipeType(), input, level, getLastUsedRecipeId());
        lookup.ifPresent(o -> setLastUsedRecipeId(o.id()));
        return lookup;
    }

    default Optional<RecipeHolder<R>> getLastUsedRecipe(@Nullable Level level)
    {
        ResourceLocation id = getLastUsedRecipeId();
        if (level == null || id == null) return Optional.empty();
        return LimaRecipesUtil.getRecipeById(level, id, getRecipeType());
    }

    default void setLastUsedRecipe(@Nullable RecipeHolder<R> lastUsedRecipe)
    {
        ResourceLocation id = lastUsedRecipe != null ? lastUsedRecipe.id() : null;
        setLastUsedRecipeId(id);
    }

    default LimaDataWatcher<Optional<ResourceLocation>> keepLastUsedSynced()
    {
        return AutomaticDataWatcher.keepNullableSynced(LimaCoreNetworkSerializers.OPTIONAL_RESOURCE_LOCATION, this::getLastUsedRecipeId, this::setLastUsedRecipeId);
    }

    final class SimpleCheck<I extends RecipeInput, R extends Recipe<I>> implements LimaRecipeCheck<I, R>
    {
        private final RecipeType<R> recipeType;
        @Nullable
        private ResourceLocation lastUsedId;

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
        public @Nullable ResourceLocation getLastUsedRecipeId()
        {
            return lastUsedId;
        }

        @Override
        public void setLastUsedRecipeId(@Nullable ResourceLocation lastUsedRecipeId)
        {
            this.lastUsedId = lastUsedRecipeId;
        }
    }
}