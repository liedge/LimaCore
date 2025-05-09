package liedge.limacore.data.generation.recipe;

import liedge.limacore.lib.ModResources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class LimaShapelessRecipeBuilder extends LimaIngredientsRecipeBuilder.SimpleBuilder<ShapelessRecipe, LimaShapelessRecipeBuilder>
{
    public LimaShapelessRecipeBuilder(ModResources modResources, ItemStack resultItem)
    {
        super(modResources, resultItem);
    }

    @Override
    protected String defaultFolderPrefix(ShapelessRecipe recipe, ResourceLocation recipeId)
    {
        return "shapeless_recipes/";
    }

    @Override
    protected ShapelessRecipe buildRecipe()
    {
        return new ShapelessRecipe("", CraftingBookCategory.MISC, resultItem, buildIngredients());
    }
}