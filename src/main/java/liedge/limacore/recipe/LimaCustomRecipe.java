package liedge.limacore.recipe;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import liedge.limacore.recipe.ingredient.ConsumeChanceIngredient;
import liedge.limacore.recipe.result.ItemResult;
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

    public static <R extends LimaCustomRecipe<?>> DataResult<R> checkNotEmpty(R recipe)
    {
        if (recipe.getItemIngredients().isEmpty() && recipe.getFluidIngredients().isEmpty())
            return DataResult.error(() -> "Recipe has no item or fluid ingredients.");
        else if (recipe.getItemResults().isEmpty() && recipe.getFluidResults().isEmpty())
            return DataResult.error(() -> "Recipe has no item or fluid output results.");
        else if (!recipe.getItemResults().isEmpty() && recipe.getItemResults().stream().noneMatch(ItemResult::requiredOutput))
        {
            return DataResult.error(() -> "Recipe must have at least 1 required item output.");
        }
        else
            return DataResult.success(recipe);
    }

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
        return itemResults.stream().map(ItemResult::getMaximumResult).collect(LimaStreamsUtil.toObjectList());
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

    private boolean shouldConsumeIngredient(Ingredient root, RandomSource random)
    {
        if (root.getCustomIngredient() instanceof ConsumeChanceIngredient chanceIngredient)
        {
            float chance = chanceIngredient.consumeChance();
            return chance != 0 && random.nextFloat() < chance;
        }

        return true;
    }

    public void consumeItemIngredients(T input, RandomSource random)
    {
        for (SizedIngredient sizedIngredient : itemIngredients)
        {
            int remaining = sizedIngredient.count();
            Ingredient root = sizedIngredient.ingredient();

            // Consumption chance happens here
            if (!shouldConsumeIngredient(root, random)) continue; // Skip entirely

            for (int slot = 0; slot < input.size(); slot++)
            {
                if (root.test(input.getItem(slot)))
                {
                    ItemStack extracted = input.extractItem(slot, remaining, false);
                    remaining -= extracted.getCount();

                    if (remaining == 0) break;
                }
            }
        }
    }

    public void consumeFluidIngredients(T input)
    {
        for (SizedFluidIngredient sizedIngredient : fluidIngredients)
        {
            int remaining = sizedIngredient.amount();
            FluidIngredient root = sizedIngredient.ingredient();

            for (int tank = 0; tank < input.tanks(); tank++)
            {
                if (root.test(input.getFluid(tank)))
                {
                    FluidStack extracted = input.extractFluid(tank, remaining, IFluidHandler.FluidAction.EXECUTE);
                    remaining -= extracted.getAmount();

                    if (remaining == 0) break;
                }
            }
        }
    }

    private boolean checkItemInputs(T input)
    {
        if (!input.checkItemInputSize(itemIngredients)) return false;

        int[] removalTracker = new int[input.size()];

        for (SizedIngredient sizedIngredient : itemIngredients)
        {
            int stillNeeded = sizedIngredient.count();
            Ingredient root = sizedIngredient.ingredient();

            for (int slot = 0; slot < input.size(); slot++)
            {
                if (root.test(input.getItem(slot)))
                {
                    int toExtract = Math.max(0, stillNeeded - removalTracker[slot]);
                    if (toExtract > 0)
                    {
                        ItemStack extracted = input.extractItem(slot, toExtract, true);
                        stillNeeded -= extracted.getCount();
                        removalTracker[slot] += extracted.getCount();

                        if (stillNeeded == 0) break;
                    }
                }
            }

            if (stillNeeded > 0) return false;
        }

        return true;
    }

    private boolean checkFluidInputs(T input)
    {
        if (!input.checkFluidInputSize(fluidIngredients)) return false;

        int[] removalTracker = new int[input.tanks()];

        for (SizedFluidIngredient sizedIngredient : fluidIngredients)
        {
            int stillNeeded = sizedIngredient.amount();
            FluidIngredient root = sizedIngredient.ingredient();

            for (int tank = 0; tank < input.tanks(); tank++)
            {
                if (root.test(input.getFluid(tank)))
                {
                    int toDrain = Math.max(0, stillNeeded - removalTracker[tank]);
                    if (toDrain > 0)
                    {
                        FluidStack extracted = input.extractFluid(tank, toDrain, IFluidHandler.FluidAction.SIMULATE);
                        stillNeeded -= extracted.getAmount();
                        removalTracker[tank] += extracted.getAmount();

                        if (stillNeeded == 0) break;
                    }
                }
            }

            if (stillNeeded > 0) return false;
        }

        return true;
    }

    @Override
    public boolean matches(T input, Level level)
    {
        return checkItemInputs(input) && checkFluidInputs(input);
    }

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

    @FunctionalInterface
    public interface RecipeFactory<R extends LimaCustomRecipe<?>> extends Function4<List<SizedIngredient>, List<SizedFluidIngredient>, List<ItemResult>, List<FluidStack>, R>
    {
        R create(List<SizedIngredient> itemIngredients, List<SizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, List<FluidStack> fluidResults);

        @Override
        default R apply(List<SizedIngredient> ingredients, List<SizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, List<FluidStack> fluidStacks)
        {
            return create(ingredients, fluidIngredients, itemResults, fluidStacks);
        }
    }
}