package liedge.limacore.recipe;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntList;
import liedge.limacore.util.LimaRecipesUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public abstract class LimaCustomRecipe<T extends LimaRecipeInput> implements Recipe<T>
{
    private final NonNullList<Ingredient> ingredients;

    protected LimaCustomRecipe(NonNullList<Ingredient> ingredients)
    {
        this.ingredients = ingredients;
    }

    protected LimaCustomRecipe(Ingredient ingredient)
    {
        this(NonNullList.withSize(1, ingredient));
    }

    public Ingredient getIngredient(int index)
    {
        Preconditions.checkElementIndex(index, ingredients.size(), "Ingredient");
        return ingredients.get(index);
    }

    public int getIngredientStackSize(int index)
    {
        return LimaRecipesUtil.getIngredientStackSize(getIngredient(index));
    }

    public boolean consumeIngredientsStrictSlots(T input, boolean requireEmptySpace, boolean simulate)
    {
        for (int i = 0; i < input.size(); i++)
        {
            if (i < ingredients.size())
            {
                Ingredient ingredient = ingredients.get(i);
                int count = LimaRecipesUtil.getIngredientStackSize(ingredient);
                ItemStack extracted = input.extractFromContainer(i, count, simulate);
                if (!ingredient.test(extracted)) return false;
            }
            else if (requireEmptySpace)
            {
                if (!input.getItem(i).isEmpty()) return false;
            }
        }

        return true;
    }

    public boolean consumeIngredientsLenientSlots(T input, boolean simulate)
    {
        for (Ingredient ingredient : ingredients)
        {
            /*
            Handle simple ingredients differently. Because ingredient.test() requires that a slot contains the ingredient's entire count,
            the regular test will fail even if the player has the necessary materials spread out over several inventory slots.
             */
            if (ingredient.isSimple())
            {
                int remaining = LimaRecipesUtil.getIngredientStackSize(ingredient);
                IntList validIds = ingredient.getStackingIds();

                for (int i = 0; i < input.size(); i++)
                {
                    if (validIds.contains(StackedContents.getStackingIndex(input.getItem(i))))
                    {
                        ItemStack extracted = input.extractFromContainer(i, remaining, simulate);
                        remaining -= extracted.getCount();

                        if (remaining == 0) break;
                    }
                }

                if (remaining > 0) return false;
            }
            else
            {
                boolean found = false;

                for (int i = 0; i < input.size(); i++)
                {
                    ItemStack toTest = input.getItem(i);
                    if (ingredient.test(toTest))
                    {
                        input.extractFromContainer(i, toTest.getCount(), simulate);
                        found = true;
                        break;
                    }
                }

                if (!found) return false;
            }
        }

        return true;
    }

    @Override
    public boolean matches(T input, Level level)
    {
        return consumeIngredientsStrictSlots(input, false, true);
    }

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

    @Deprecated
    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return false;
    }
}