package liedge.limacore.recipe;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public abstract class LimaSizedIngredientRecipe<T extends LimaRecipeInput> implements Recipe<T>
{
    public static final String EMPTY_GROUP = "";
    public static final MapCodec<String> GROUP_MAP_CODEC = Codec.STRING.optionalFieldOf("group", EMPTY_GROUP);

    private final List<SizedIngredient> recipeIngredients;

    protected LimaSizedIngredientRecipe(List<SizedIngredient> recipeIngredients)
    {
        this.recipeIngredients = recipeIngredients;
    }

    protected LimaSizedIngredientRecipe(SizedIngredient ingredient)
    {
        this(List.of(ingredient));
    }

    public List<SizedIngredient> getRecipeIngredients()
    {
        return recipeIngredients;
    }

    public SizedIngredient getRecipeIngredient(int index)
    {
        Preconditions.checkElementIndex(index, recipeIngredients.size(), "Recipe Ingredient");
        return recipeIngredients.get(index);
    }

    public int getRecipeIngredientCount(int index)
    {
        return getRecipeIngredient(index).count();
    }

    public boolean consumeIngredientsStrictSlots(T input, boolean requireEmptySpaces, boolean simulate)
    {
        for (int i = 0; i < input.size(); i++)
        {
            if (i < recipeIngredients.size())
            {
                SizedIngredient sizedIngredient = recipeIngredients.get(i);
                int count = sizedIngredient.count();
                ItemStack extracted = input.extractFromContainer(i, count, simulate);
                if (!sizedIngredient.test(extracted)) return false;
            }
            else if (requireEmptySpaces)
            {
                if (!input.getItem(i).isEmpty()) return false;
            }
        }

        return true;
    }

    public boolean consumeIngredientsLenientSlots(T input, boolean simulate)
    {
        for (SizedIngredient sizedIngredient : recipeIngredients)
        {
            int remaining = sizedIngredient.count();
            Ingredient root = sizedIngredient.ingredient();

            for (int i = 0; i < input.size(); i++)
            {
                if (root.test(input.getItem(i)))
                {
                    ItemStack extracted = input.extractFromContainer(i, remaining, simulate);
                    remaining -= extracted.getCount();

                    if (remaining == 0) break;
                }
            }

            if (remaining > 0) return false;
        }

        return true;
    }

    @Deprecated
    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        return Recipe.super.getIngredients();
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
    public boolean isIncomplete()
    {
        return recipeIngredients.isEmpty() || recipeIngredients.stream().map(SizedIngredient::ingredient).anyMatch(Ingredient::hasNoItems);
    }

    @Deprecated
    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return false;
    }
}