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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;

public abstract class LimaCustomRecipe<T extends LimaRecipeInput> implements Recipe<T>
{
    public static final String EMPTY_GROUP = "";
    public static final MapCodec<String> GROUP_MAP_CODEC = Codec.STRING.optionalFieldOf("group", EMPTY_GROUP);

    // Ingredients
    private final List<SizedIngredient> itemIngredients;
    private final List<SizedFluidIngredient> fluidIngredients;

    // Results
    private final List<ItemResult> itemResults;
    private final List<FluidStack> fluidResults;

    protected LimaCustomRecipe(List<SizedIngredient> itemIngredients, List<SizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, List<FluidStack> fluidResults)
    {
        this.itemIngredients = itemIngredients;
        this.fluidIngredients = fluidIngredients;
        this.itemResults = itemResults;
        this.fluidResults = fluidResults;
    }

    protected LimaCustomRecipe(List<SizedIngredient> itemIngredients, List<ItemResult> itemResults)
    {
        this(itemIngredients, List.of(), itemResults, List.of());
    }

    //#region Ingredient functions
    public List<SizedIngredient> getItemIngredients()
    {
        return itemIngredients;
    }

    public List<SizedFluidIngredient> getFluidIngredients()
    {
        return fluidIngredients;
    }

    public SizedIngredient getItemIngredient(int index)
    {
        Preconditions.checkElementIndex(index, itemIngredients.size(), "Item Ingredient");
        return itemIngredients.get(index);
    }

    public SizedFluidIngredient getFluidIngredient(int index)
    {
        Preconditions.checkElementIndex(index, fluidIngredients.size(), "Fluid Ingredient");
        return fluidIngredients.get(index);
    }
    //#endregion

    //#region Result functions
    public List<ItemResult> getItemResults()
    {
        return itemResults;
    }

    public ItemResult getItemResult(int index)
    {
        Preconditions.checkElementIndex(index, itemResults.size(), "Item Result");
        return itemResults.get(index);
    }

    /**
     * Convenience accessor for the first {@link ItemResult} of this recipe. For use in single output
     * recipes.
     * @return The first item result.
     */
    public ItemResult getFirstItemResult()
    {
        Preconditions.checkState(!itemResults.isEmpty(), "Recipe has no item results.");
        return itemResults.getFirst();
    }

    public List<ItemStack> generateItemResults(T input, HolderLookup.Provider registries, RandomSource random)
    {
        return itemResults.stream().map(r -> r.generateResult(random)).filter(s -> !s.isEmpty()).collect(LimaStreamsUtil.toObjectList());
    }

    public List<ItemStack> getPossibleItemResults()
    {
        return itemResults.stream().map(ItemResult::item).collect(LimaStreamsUtil.toObjectList());
    }

    public List<FluidStack> getFluidResults()
    {
        return fluidResults;
    }

    public FluidStack getFluidResult(int index)
    {
        Preconditions.checkElementIndex(index, fluidResults.size(), "Fluid results");
        return fluidResults.get(index);
    }

    /**
     * Convenience accessor for the first {@link FluidStack} of this recipe's fluid results. For use in single output
     * recipes.
     * @return The first fluid result.
     */
    public FluidStack getFirstFluidResult()
    {
        Preconditions.checkState(!fluidResults.isEmpty(), "Recipe has no fluid results.");
        return fluidResults.getFirst();
    }

    public List<FluidStack> generateFluidResults(T input, HolderLookup.Provider registries)
    {
        return fluidResults.stream().map(FluidStack::copy).collect(LimaStreamsUtil.toObjectList());
    }
    //#endregion

    public boolean consumeItemIngredients(T input, boolean simulate)
    {
        if (!input.checkItemInputSize(itemIngredients)) return false;

        for (SizedIngredient sizedIngredient : itemIngredients)
        {
            int remaining = sizedIngredient.count();
            Ingredient root = sizedIngredient.ingredient();

            for (int i = 0; i < input.size(); i++)
            {
                if (root.test(input.getItem(i)))
                {
                    ItemStack extracted = input.extractItem(i, remaining, simulate);
                    remaining -= extracted.getCount();

                    if (remaining == 0) break;
                }
            }

            if (remaining > 0) return false;
        }

        return true;
    }

    public boolean consumeFluidIngredients(T input, IFluidHandler.FluidAction action)
    {
        if (!input.checkFluidInputSize(fluidIngredients)) return false;

        for (SizedFluidIngredient fluidIngredient : fluidIngredients)
        {
            int remaining = fluidIngredient.amount();
            FluidIngredient root = fluidIngredient.ingredient();

            for (int i = 0; i < input.tanks(); i++)
            {
                if (root.test(input.getFluid(i)))
                {
                    FluidStack extracted = input.extractFluid(i, remaining, action);
                    remaining -= extracted.getAmount();

                    if (remaining == 0) break;
                }
            }

            if (remaining > 0) return false;
        }

        return true;
    }

    @Override
    public abstract boolean matches(T input, Level level);

    @Override
    public boolean isSpecial()
    {
        return true;
    }

    @Override
    public boolean isIncomplete()
    {
        // Seems like only the client recipe book uses this?
        return itemIngredients.isEmpty() || itemIngredients.stream().map(SizedIngredient::ingredient).anyMatch(Ingredient::hasNoItems);
    }

    /**
     * @deprecated Use {@link LimaCustomRecipe#getItemIngredients()} for the recipe's item ingredients and {@link LimaCustomRecipe#getFluidIngredients()}
     * for the fluid ingredients.
     */
    @Deprecated
    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        return Recipe.super.getIngredients();
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