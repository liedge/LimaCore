package liedge.limacore.data.generation.recipe;

import liedge.limacore.lib.ModResources;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class LimaShapelessRecipeBuilder extends VanillaRecipeBuilder.StandardIngredients<ShapelessRecipe, CraftingBookCategory, CraftingRecipe.CraftingBookInfo, LimaShapelessRecipeBuilder>
{
    public LimaShapelessRecipeBuilder(ModResources modResources, ItemStackTemplate resultItem)
    {
        super(modResources, resultItem, CraftingBookCategory.MISC, CraftingRecipe.CraftingBookInfo::new);
    }

    @Override
    protected String defaultFolderPrefix(ShapelessRecipe recipe)
    {
        return "shapeless_recipes/";
    }

    @Override
    protected ShapelessRecipe buildRecipe(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo categoryInfo, ItemStackTemplate result)
    {
        return new ShapelessRecipe(commonInfo, categoryInfo, result, ingredients);
    }
}