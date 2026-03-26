package liedge.limacore.data.generation.recipe;

import com.google.common.base.Preconditions;
import liedge.limacore.lib.ModResources;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public class LimaStonecuttingRecipeBuilder extends VanillaRecipeBuilder.StandardIngredients<StonecutterRecipe, CraftingBookCategory, CraftingRecipe.CraftingBookInfo, LimaStonecuttingRecipeBuilder>
{
    public LimaStonecuttingRecipeBuilder(ModResources modResources, ItemStackTemplate resultItem)
    {
        super(modResources, resultItem, CraftingBookCategory.MISC, CraftingRecipe.CraftingBookInfo::new);
    }

    @Override
    protected StonecutterRecipe buildRecipe(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo categoryInfo, ItemStackTemplate result)
    {
        Preconditions.checkState(ingredients.size() == 1, "Stonecutting recipe must have exactly 1 ingredient.");
        return new StonecutterRecipe(commonInfo, ingredients.getFirst(), result);
    }
}