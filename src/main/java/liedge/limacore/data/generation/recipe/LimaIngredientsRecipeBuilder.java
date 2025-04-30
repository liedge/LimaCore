package liedge.limacore.data.generation.recipe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.ModResources;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public abstract class LimaIngredientsRecipeBuilder<R extends Recipe<?>, B extends LimaIngredientsRecipeBuilder<R, B>> extends LimaRecipeBuilder<R, B>
{
    protected LimaIngredientsRecipeBuilder(ModResources modResources)
    {
        super(modResources);
    }

    public abstract B input(Ingredient ingredient);

    public B input(ItemLike item)
    {
        return input(Ingredient.of(item));
    }

    public B input(TagKey<Item> tagKey)
    {
        return input(Ingredient.of(tagKey));
    }

    public static abstract class SimpleBuilder<R extends Recipe<?>, B extends SimpleBuilder<R, B>> extends LimaIngredientsRecipeBuilder<R, B>
    {
        protected final List<Ingredient> ingredients = new ObjectArrayList<>();
        protected final ItemStack resultItem;

        protected SimpleBuilder(ModResources modResources, ItemStack resultItem)
        {
            super(modResources);
            this.resultItem = resultItem;
        }

        @Override
        public B input(Ingredient ingredient)
        {
            ingredients.add(ingredient);
            return selfUnchecked();
        }

        @Override
        protected String getDefaultRecipeName()
        {
            return getDefaultStackName(resultItem);
        }

        protected NonNullList<Ingredient> buildIngredients()
        {
            return NonNullList.copyOf(ingredients);
        }
    }
}