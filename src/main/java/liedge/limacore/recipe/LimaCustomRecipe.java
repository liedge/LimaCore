package liedge.limacore.recipe;

import com.google.common.base.Preconditions;
import liedge.limacore.util.LimaCollectionsUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

public abstract class LimaCustomRecipe<T extends RecipeInput> implements Recipe<T>
{
    private final NonNullList<Ingredient> ingredients;

    protected LimaCustomRecipe(NonNullList<Ingredient> ingredients)
    {
        this.ingredients = ingredients;
    }

    protected LimaCustomRecipe(Ingredient ingredient)
    {
        this(LimaCollectionsUtil.nonNullListOf(ingredient));
    }

    public Ingredient getIngredient(int index)
    {
        Preconditions.checkElementIndex(index, ingredients.size(), "Ingredient");
        return ingredients.get(index);
    }

    public int getIngredientStackSize(int index)
    {
        Ingredient ingredient = getIngredient(index);
        if (ingredient.getCustomIngredient() instanceof LimaSimpleCountIngredient countIngredient)
        {
            return countIngredient.getIngredientCount();
        }
        else
        {
            return 1;
        }
    }

    @Override
    public abstract boolean matches(T recipeInput, Level level);

    @Override
    public abstract ItemStack assemble(T recipeInput, HolderLookup.Provider registries);

    @Override
    public boolean isSpecial()
    {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        return ingredients;
    }

    @Override @Deprecated
    public boolean canCraftInDimensions(int width, int height)
    {
        throw new UnsupportedOperationException("Not supported for Lima custom recipes");
    }
}