package liedge.limacore.util;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.recipe.ItemResult;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeSerializer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class LimaRecipesUtil
{
    private LimaRecipesUtil() {}

    @SuppressWarnings("unchecked")
    public static <T extends Recipe<?>> Optional<RecipeHolder<T>> getRecipeById(Level level, ResourceLocation recipeId, RecipeType<T> recipeType)
    {
        return level.getRecipeManager().byKey(recipeId).filter(holder -> holder.value().getType().equals(recipeType)).map(h -> (RecipeHolder<T>) h);
    }

    public static <T extends Recipe<?>> Optional<RecipeHolder<T>> getRecipeById(Level level, ResourceLocation recipeId, Supplier<? extends RecipeType<T>> typeSupplier)
    {
        return getRecipeById(level, recipeId, typeSupplier.get());
    }

    public static <T extends RecipeInput, R extends Recipe<T>> List<RecipeHolder<R>> getSortedRecipesForType(Level level, RecipeType<R> recipeType, Comparator<RecipeHolder<R>> comparator)
    {
        return level.getRecipeManager().getAllRecipesFor(recipeType).stream().sorted(comparator).collect(LimaStreamsUtil.toUnmodifiableObjectList());
    }

    public static <T extends RecipeInput, R extends Recipe<T>> List<RecipeHolder<R>> getSortedRecipesForType(Level level, RecipeType<R> recipeType, Comparator<RecipeHolder<R>> primary, Comparator<RecipeHolder<R>> secondary)
    {
        return getSortedRecipesForType(level, recipeType, primary.thenComparing(secondary));
    }

    public static <T extends RecipeInput, R extends Recipe<T>> List<RecipeHolder<R>> getSortedRecipesForType(Level level, Supplier<? extends RecipeType<R>> typeSupplier, Comparator<RecipeHolder<R>> comparator)
    {
        return getSortedRecipesForType(level, typeSupplier.get(), comparator);
    }

    public static <T extends RecipeInput, R extends Recipe<T>> List<RecipeHolder<R>> getSortedRecipesForType(Level level, Supplier<? extends RecipeType<R>> typeSupplier, Comparator<RecipeHolder<R>> primary, Comparator<RecipeHolder<R>> secondary)
    {
        return getSortedRecipesForType(level, typeSupplier.get(), primary, secondary);
    }

    // Codec helper factories
    public static <R extends LimaCustomRecipe<?>> LimaRecipeSerializer<R> simpleCustomSerializer(ResourceLocation id, BiFunction<List<SizedIngredient>, List<ItemResult>, R> factory, int maxIngredients, int maxResults)
    {
        MapCodec<R> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                LimaCoreCodecs.sizedIngredients(maxIngredients).forGetter(LimaCustomRecipe::getItemIngredients),
                ItemResult.listMapCodec(maxResults).forGetter(LimaCustomRecipe::getItemResults))
                .apply(instance, factory));
        StreamCodec<RegistryFriendlyByteBuf, R> streamCodec = StreamCodec.composite(
                LimaStreamCodecs.sizedIngredients(maxIngredients),
                LimaCustomRecipe::getItemIngredients,
                ItemResult.listStreamCodec(maxResults),
                LimaCustomRecipe::getItemResults,
                factory);

        return new LimaRecipeSerializer<>(id, codec, streamCodec);
    }
}