package liedge.limacore.data.generation.recipe;

import com.google.common.base.Preconditions;
import liedge.limacore.lib.ModResources;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class LimaShapelessRecipeBuilder extends LimaCustomRecipeBuilder<ShapelessRecipe, LimaShapelessRecipeBuilder>
{
    public LimaShapelessRecipeBuilder(ModResources modResources, ItemStack result)
    {
        super(RecipeSerializer.SHAPELESS_RECIPE, modResources, result);
    }

    @Override
    protected void validate(ResourceLocation id)
    {
        Preconditions.checkState(!ingredients.isEmpty(), "Shapeless recipe '" + id + "' has no inputs");
    }

    @Override
    protected ShapelessRecipe buildRecipe()
    {
        return new ShapelessRecipe("", CraftingBookCategory.MISC, result, NonNullList.copyOf(ingredients));
    }
}