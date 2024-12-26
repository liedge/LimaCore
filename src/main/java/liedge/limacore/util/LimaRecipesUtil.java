package liedge.limacore.util;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public final class LimaRecipesUtil
{
    private LimaRecipesUtil() {}

    @SuppressWarnings("unchecked")
    public static <T extends Recipe<?>> @Nullable RecipeHolder<T> recipeHolderByKey(Level level, ResourceLocation recipeId, RecipeType<T> recipeType)
    {
        Optional<RecipeHolder<?>> recipeHolder = level.getRecipeManager().byKey(recipeId).filter(holder -> holder.value().getType().equals(recipeType));
        return (RecipeHolder<T>) recipeHolder.orElse(null);
    }

    public static <T extends Recipe<?>> @Nullable RecipeHolder<T> recipeHolderByKey(Level level, ResourceLocation recipeId, Supplier<? extends RecipeType<T>> typeSupplier)
    {
        return recipeHolderByKey(level, recipeId, typeSupplier.get());
    }

    @SuppressWarnings("unchecked")
    public static <T extends Recipe<?>, U extends T> @Nullable RecipeHolder<T> superclassRecipeHolderByKey(Level level, ResourceLocation recipeId, RecipeType<U> recipeSubType)
    {
        Optional<RecipeHolder<?>> recipeHolder = level.getRecipeManager().byKey(recipeId).filter(holder -> holder.value().getType().equals(recipeSubType));
        return (RecipeHolder<T>) recipeHolder.orElse(null);
    }

    public static <T extends Recipe<?>, U extends T> @Nullable RecipeHolder<T> superclassRecipeHolderByKey(Level level, ResourceLocation recipeId, Supplier<? extends RecipeType<U>> subTypeSupplier)
    {
        return superclassRecipeHolderByKey(level, recipeId, subTypeSupplier.get());
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

    public static Ingredient getIngredientByIndex(Recipe<?> recipe, int index)
    {
        List<Ingredient> ingredients = recipe.getIngredients();
        Preconditions.checkElementIndex(index, ingredients.size(), "Recipe ingredient");
        return ingredients.get(index);
    }
}