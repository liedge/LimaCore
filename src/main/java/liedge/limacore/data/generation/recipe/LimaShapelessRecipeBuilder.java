package liedge.limacore.data.generation.recipe;

import com.google.common.base.Preconditions;
import liedge.limacore.lib.ModResources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class LimaShapelessRecipeBuilder extends LimaCustomRecipeBuilder<ShapelessRecipe, LimaShapelessRecipeBuilder>
{
    private final ItemStack resultItem;

    public LimaShapelessRecipeBuilder(ModResources modResources, ItemStack resultItem)
    {
        super(modResources);
        this.resultItem = resultItem;
    }

    @Override
    protected String defaultFolderPrefix(ShapelessRecipe recipe, ResourceLocation recipeId)
    {
        return "shapeless_recipes/";
    }

    @Override
    protected void validate(ResourceLocation id)
    {
        Preconditions.checkState(!ingredients.isEmpty(), "Shapeless recipe '" + id + "' has no inputs");
    }

    @Override
    protected ShapelessRecipe buildRecipe()
    {
        return new ShapelessRecipe("", CraftingBookCategory.MISC, resultItem, buildIngredients());
    }

    @Override
    protected String getDefaultRecipeName()
    {
        return getDefaultStackName(resultItem);
    }
}