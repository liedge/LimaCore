package liedge.limacore.data.generation.recipe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.ModResources;
import liedge.limacore.recipe.LimaSimpleCountIngredient;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class LimaCustomRecipeBuilder<R extends Recipe<?>, B extends LimaCustomRecipeBuilder<R, B>> extends LimaBaseRecipeBuilder<R, B>
{
    public static <R extends Recipe<?>> SimpleBuilder<R> simpleBuilder(RecipeSerializer<R> serializer, ModResources modResources, ItemStack result, BiFunction<NonNullList<Ingredient>, ItemStack, R> recipeFactory)
    {
        return new SimpleBuilder<>(serializer, modResources, result, recipeFactory);
    }

    public static <R extends Recipe<?>> SimpleBuilder<R> simpleBuilder(Supplier<? extends RecipeSerializer<R>> serializer, ModResources modResources, ItemStack result, BiFunction<NonNullList<Ingredient>, ItemStack, R> recipeFactory)
    {
        return simpleBuilder(serializer.get(), modResources, result, recipeFactory);
    }

    protected final ItemStack result;
    protected final List<Ingredient> ingredients = new ObjectArrayList<>();

    protected LimaCustomRecipeBuilder(RecipeSerializer<? extends R> serializer, ModResources modResources, ItemStack result)
    {
        super(serializer, modResources);
        this.result = result;
    }

    protected LimaCustomRecipeBuilder(Supplier<? extends RecipeSerializer<R>> supplier, ModResources modResources, ItemStack result)
    {
        this(supplier.get(), modResources, result);
    }

    public B input(Ingredient ingredient)
    {
        ingredients.add(ingredient);
        return selfUnchecked();
    }

    public B input(ItemLike item, int count)
    {
        return input(LimaSimpleCountIngredient.itemValue(item, count));
    }

    public B input(ItemLike item)
    {
        return input(item, 1);
    }

    public B input(TagKey<Item> itemTag, int count)
    {
        return input(LimaSimpleCountIngredient.tagValue(itemTag, count));
    }

    public B input(TagKey<Item> itemTag)
    {
        return input(itemTag, 1);
    }

    @Override
    protected String getDefaultRecipeName()
    {
        return LimaRegistryUtil.getItemName(result.getItem());
    }

    public static class SimpleBuilder<R extends Recipe<?>> extends LimaCustomRecipeBuilder<R, SimpleBuilder<R>>
    {
        private final BiFunction<NonNullList<Ingredient>, ItemStack, R> recipeFactory;

        protected SimpleBuilder(RecipeSerializer<? extends R> serializer, ModResources modResources, ItemStack result, BiFunction<NonNullList<Ingredient>, ItemStack, R> recipeFactory)
        {
            super(serializer, modResources, result);
            this.recipeFactory = recipeFactory;
        }

        @Override
        protected void validate(ResourceLocation id) { }

        @Override
        protected R buildRecipe()
        {
            return recipeFactory.apply(NonNullList.copyOf(ingredients), result);
        }
    }
}