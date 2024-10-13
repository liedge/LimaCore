package liedge.limacore.data.generation.recipe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.ModResources;
import liedge.limacore.recipe.ingredient.LimaCustomCountIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public abstract class LimaCustomRecipeBuilder<R extends Recipe<?>, B extends LimaCustomRecipeBuilder<R, B>> extends LimaBaseRecipeBuilder<R, B>
{
    protected final List<Ingredient> ingredients = new ObjectArrayList<>();

    protected LimaCustomRecipeBuilder(ModResources modResources)
    {
        super(modResources);
    }

    public B input(Ingredient ingredient)
    {
        ingredients.add(ingredient);
        return selfUnchecked();
    }

    public B input(ItemLike item, int count)
    {
        return input(LimaCustomCountIngredient.of(item, count));
    }

    public B input(ItemLike item)
    {
        return input(item, 1);
    }

    public B input(TagKey<Item> itemTag, int count)
    {
        return input(LimaCustomCountIngredient.of(itemTag, count));
    }

    public B input(TagKey<Item> itemTag)
    {
        return input(itemTag, 1);
    }

    protected NonNullList<Ingredient> buildIngredients()
    {
        return NonNullList.copyOf(ingredients);
    }
}