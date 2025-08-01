package liedge.limacore.data.generation.recipe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.ModResources;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public abstract class StandardRecipeBuilder<R extends Recipe<?>, B extends StandardRecipeBuilder<R, B>> extends LimaRecipeBuilder<R, B>
{
    protected final List<Ingredient> ingredients = new ObjectArrayList<>();
    protected final ItemStack result;

    protected StandardRecipeBuilder(ModResources resources, ItemStack result)
    {
        super(resources);
        this.result = result;
    }

    public B input(Ingredient ingredient)
    {
        ingredients.add(ingredient);
        return selfUnchecked();
    }

    public B input(ItemLike item)
    {
        return input(Ingredient.of(item));
    }

    public B input(TagKey<Item> tagKey)
    {
        return input(Ingredient.of(tagKey));
    }

    @Override
    protected String getDefaultRecipeName()
    {
        return LimaRegistryUtil.getItemName(result);
    }
}