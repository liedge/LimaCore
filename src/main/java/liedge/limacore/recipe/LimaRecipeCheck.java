package liedge.limacore.recipe;

import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.network.sync.NullableValueTracker;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public final class LimaRecipeCheck<I extends RecipeInput, R extends Recipe<I>> implements RecipeManager.CachedCheck<I, R>, ValueIOSerializable
{
    public static <I extends RecipeInput, R extends Recipe<I>> LimaRecipeCheck<I, R> create(RecipeType<R> recipeType)
    {
        return new LimaRecipeCheck<>(recipeType);
    }

    public static <I extends RecipeInput, R extends Recipe<I>> LimaRecipeCheck<I, R> create(Supplier<? extends RecipeType<R>> typeSupplier)
    {
        return new LimaRecipeCheck<>(typeSupplier.get());
    }

    private final RecipeType<R> recipeType;
    private @Nullable ResourceKey<Recipe<?>> lastUsedRecipeKey;

    private LimaRecipeCheck(RecipeType<R> recipeType)
    {
        this.recipeType = recipeType;
    }

    public RecipeType<R> getRecipeType()
    {
        return recipeType;
    }

    public @Nullable ResourceKey<Recipe<?>> getLastUsedRecipeKey()
    {
        return lastUsedRecipeKey;
    }

    public void setLastUsedRecipeKey(@Nullable ResourceKey<Recipe<?>> lastUsedRecipeKey)
    {
        this.lastUsedRecipeKey = lastUsedRecipeKey;
    }

    @Override
    public Optional<RecipeHolder<R>> getRecipeFor(I input, ServerLevel level)
    {
        Optional<RecipeHolder<R>> lookup = level.recipeAccess().getRecipeFor(recipeType, input, level, lastUsedRecipeKey);
        lookup.ifPresent(o -> setLastUsedRecipeKey(o.id()));
        return lookup;
    }

    public Optional<RecipeHolder<R>> getLastUsedRecipe(ServerLevel level)
    {
        if (lastUsedRecipeKey == null) return Optional.empty();
        else return LimaRegistryUtil.getRecipeByKey(level, lastUsedRecipeKey, recipeType);
    }

    public void setLastUsedRecipe(@Nullable RecipeHolder<R> lastUsedRecipe)
    {
        ResourceKey<Recipe<?>> key = lastUsedRecipe != null ? lastUsedRecipe.id() : null;
        setLastUsedRecipeKey(key);
    }

    public LimaDataWatcher<Optional<ResourceKey<Recipe<?>>>> keepLastUsedSynced()
    {
        return NullableValueTracker.create(LimaCoreNetworkSerializers.OPTIONAL_RECIPE_KEY, this::getLastUsedRecipeKey, this::setLastUsedRecipeKey).setAutomatic();
    }

    @Override
    public void serialize(ValueOutput output)
    {
        output.storeNullable("recipe", Recipe.KEY_CODEC, lastUsedRecipeKey);
    }

    @Override
    public void deserialize(ValueInput input)
    {
        this.lastUsedRecipeKey = input.read("recipe", Recipe.KEY_CODEC).orElse(null);
    }
}