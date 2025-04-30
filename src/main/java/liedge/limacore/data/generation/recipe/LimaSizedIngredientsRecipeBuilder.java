package liedge.limacore.data.generation.recipe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.ModResources;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;
import java.util.function.BiFunction;

public abstract class LimaSizedIngredientsRecipeBuilder<R extends Recipe<?>, B extends LimaSizedIngredientsRecipeBuilder<R, B>> extends LimaRecipeBuilder<R, B>
{
    public static <R extends Recipe<?>, B extends SimpleBuilder<R, B>> SimpleBuilder<R, B> simpleBuilder(ModResources resources, ItemStack resultItem, BiFunction<List<SizedIngredient>, ItemStack, R> factory)
    {
        return new SimpleBuilder<>(resources, resultItem)
        {
            @Override
            protected R buildRecipe()
            {
                return factory.apply(ingredients, resultItem);
            }
        };
    }

    protected final List<SizedIngredient> ingredients = new ObjectArrayList<>();

    protected LimaSizedIngredientsRecipeBuilder(ModResources modResources)
    {
        super(modResources);
    }

    public B input(SizedIngredient sizedIngredient)
    {
        ingredients.add(sizedIngredient);
        return selfUnchecked();
    }

    public B input(Ingredient ingredient)
    {
        return input(new SizedIngredient(ingredient, 1));
    }

    public B input(Ingredient ingredient, int count)
    {
        return input(new SizedIngredient(ingredient, count));
    }

    public B input(ItemLike itemLike)
    {
        return input(Ingredient.of(itemLike));
    }

    public B input(ItemLike itemLike, int count)
    {
        return input(SizedIngredient.of(itemLike, count));
    }

    public B input(TagKey<Item> tagKey)
    {
        return input(Ingredient.of(tagKey));
    }

    public B input(TagKey<Item> tagKey, int count)
    {
        return input(SizedIngredient.of(tagKey, count));
    }

    public static abstract class SimpleBuilder<R extends Recipe<?>, B extends SimpleBuilder<R, B>> extends LimaSizedIngredientsRecipeBuilder<R, B>
    {
        protected final ItemStack resultItem;

        protected SimpleBuilder(ModResources modResources, ItemStack resultItem)
        {
            super(modResources);
            this.resultItem = resultItem;
        }

        @Override
        protected String getDefaultRecipeName()
        {
            return getDefaultStackName(resultItem);
        }
    }
}