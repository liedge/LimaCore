package liedge.limacore.recipe;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.util.LimaStreamsUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public abstract class LimaCustomRecipe<T extends LimaRecipeInput> implements Recipe<T>
{
    public static final String EMPTY_GROUP = "";
    public static final MapCodec<String> GROUP_MAP_CODEC = Codec.STRING.optionalFieldOf("group", EMPTY_GROUP);

    // Ingredients
    private final List<SizedIngredient> itemIngredients;

    // Results
    private final List<ItemResult> itemResults;

    protected LimaCustomRecipe(List<SizedIngredient> itemIngredients, List<ItemResult> itemResults)
    {
        this.itemIngredients = itemIngredients;
        this.itemResults = itemResults;
    }

    // Item ingredient functions
    public List<SizedIngredient> getItemIngredients()
    {
        return itemIngredients;
    }

    public SizedIngredient getItemIngredient(int index)
    {
        Preconditions.checkElementIndex(index, itemIngredients.size(), "Item Ingredient");
        return itemIngredients.get(index);
    }

    public int getItemIngredientCount(int index)
    {
        return getItemIngredient(index).count();
    }

    // Item result functions
    public List<ItemResult> getItemResults()
    {
        return itemResults;
    }

    /**
     * Convenience method to return the {@link ItemResult} from the results list, for use in single output recipes.
     * @return The first result
     */
    public ItemResult getFirstResult()
    {
        return itemResults.getFirst();
    }

    public ItemResult getItemResult(int index)
    {
        Preconditions.checkElementIndex(index, itemResults.size(), "Item Result");
        return itemResults.get(index);
    }

    public List<ItemStack> generateItemResults(T input, HolderLookup.Provider registries, RandomSource random)
    {
        return itemResults.stream().map(r -> r.generateResult(random)).filter(s -> !s.isEmpty()).collect(LimaStreamsUtil.toObjectList());
    }

    public List<ItemStack> getPossibleItemResults()
    {
        return itemResults.stream().map(ItemResult::item).collect(LimaStreamsUtil.toObjectList());
    }

    public boolean consumeIngredientsStrictSlots(T input, boolean requireEmptySpaces, boolean simulate)
    {
        for (int i = 0; i < input.size(); i++)
        {
            if (i < itemIngredients.size())
            {
                SizedIngredient sizedIngredient = itemIngredients.get(i);
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
        for (SizedIngredient sizedIngredient : itemIngredients)
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
        return itemIngredients.isEmpty() || itemIngredients.stream().map(SizedIngredient::ingredient).anyMatch(Ingredient::hasNoItems);
    }

    /**
     * @deprecated Use {@link LimaCustomRecipe#generateItemResults(LimaRecipeInput, HolderLookup.Provider, RandomSource)} to create
     * recipe item outputs.
     */
    @Deprecated
    @Override
    public ItemStack assemble(T input, HolderLookup.Provider registries)
    {
        return ItemStack.EMPTY;
    }

    /**
     * @deprecated Use {@link LimaCustomRecipe#getItemResults()} for item recipe outputs.
     */
    @Deprecated
    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return ItemStack.EMPTY;
    }

    /**
     * @deprecated Not used or compatible with custom recipes.
     */
    @Deprecated
    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return false;
    }
}