package liedge.limacore.data.generation.recipe;

import liedge.limacore.lib.ModResources;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class LimaShapelessRecipeBuilder extends StandardRecipeBuilder<ShapelessRecipe, LimaShapelessRecipeBuilder>
{
    private CraftingBookCategory category = CraftingBookCategory.MISC;

    public LimaShapelessRecipeBuilder(ModResources modResources, ItemStack resultItem)
    {
        super(modResources, resultItem);
    }

    public LimaShapelessRecipeBuilder bookCategory(CraftingBookCategory category)
    {
        this.category = category;
        return this;
    }

    @Override
    protected String defaultFolderPrefix(ShapelessRecipe recipe, ResourceLocation recipeId)
    {
        return "shapeless_recipes/";
    }

    @Override
    protected ShapelessRecipe buildRecipe()
    {
        return new ShapelessRecipe(getGroupOrBlank(), category, result, NonNullList.copyOf(ingredients));
    }
}