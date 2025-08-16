package liedge.limacore.data.generation.recipe;

import com.google.common.base.Preconditions;
import liedge.limacore.lib.ModResources;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public class LimaStonecuttingRecipeBuilder extends StandardRecipeBuilder<StonecutterRecipe, LimaStonecuttingRecipeBuilder>
{
    public LimaStonecuttingRecipeBuilder(ModResources modResources, ItemStack resultItem)
    {
        super(modResources, resultItem);
    }

    @Override
    protected StonecutterRecipe buildRecipe()
    {
        Preconditions.checkState(ingredients.size() == 1, "Stonecutting recipe must have exactly 1 ingredient.");
        return new StonecutterRecipe(getGroupOrBlank(), ingredients.getFirst(), result);
    }
}