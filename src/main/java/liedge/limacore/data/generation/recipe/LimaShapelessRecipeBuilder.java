package liedge.limacore.data.generation.recipe;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.ModResources;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.List;

public class LimaShapelessRecipeBuilder extends SingleResultRecipeBuilder<ShapelessRecipe, LimaShapelessRecipeBuilder>
{
    private final List<Ingredient> ingredients = new ObjectArrayList<>();

    public LimaShapelessRecipeBuilder(ModResources resources, ItemStack result)
    {
        super(RecipeSerializer.SHAPELESS_RECIPE, resources, result);
    }

    public LimaShapelessRecipeBuilder input(Ingredient ingredient)
    {
        ingredients.add(ingredient);
        return this;
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