package liedge.limacore.recipe;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import liedge.limacore.recipe.ingredient.LimaSizedFluidIngredient;
import liedge.limacore.recipe.ingredient.LimaSizedIngredient;
import liedge.limacore.recipe.ingredient.LimaSizedItemIngredient;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import liedge.limacore.recipe.result.ResultPriority;
import liedge.limacore.util.LimaStreamsUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public abstract class LimaCustomRecipe<T extends LimaRecipeInput> implements Recipe<T>
{
    public static final String EMPTY_GROUP = "";
    public static final MapCodec<String> GROUP_MAP_CODEC = Codec.STRING.optionalFieldOf("group", EMPTY_GROUP);

    public static <R extends LimaCustomRecipe<?>> DataResult<R> checkNotEmpty(R recipe)
    {
        if (recipe.getItemIngredients().isEmpty() && recipe.getFluidIngredients().isEmpty())
            return DataResult.error(() -> "Recipe has no item or fluid ingredients.");
        else if (recipe.getItemResults().isEmpty() && recipe.getFluidResults().isEmpty())
            return DataResult.error(() -> "Recipe has no item or fluid results.");
        else if (Stream.concat(recipe.getItemResults().stream(), recipe.getFluidResults().stream()).noneMatch(sbr -> sbr.getPriority() == ResultPriority.PRIMARY))
        {
            return DataResult.error(() -> "At least one result (item or fluid) must be designated as primary.");
        }
        else
            return DataResult.success(recipe);
    }

    // Ingredients
    private final List<LimaSizedItemIngredient> itemIngredients;
    private final List<LimaSizedFluidIngredient> fluidIngredients;

    // Results
    private final List<ItemResult> itemResults;
    private final List<FluidResult> fluidResults;

    protected LimaCustomRecipe(List<LimaSizedItemIngredient> itemIngredients, List<LimaSizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, List<FluidResult> fluidResults)
    {
        this.itemIngredients = itemIngredients;
        this.fluidIngredients = fluidIngredients;
        this.itemResults = itemResults;
        this.fluidResults = fluidResults;
    }

    protected LimaCustomRecipe(List<LimaSizedItemIngredient> itemIngredients, List<ItemResult> itemResults)
    {
        this(itemIngredients, List.of(), itemResults, List.of());
    }

    //#region Ingredient functions
    public List<LimaSizedItemIngredient> getItemIngredients()
    {
        return itemIngredients;
    }

    public List<LimaSizedFluidIngredient> getFluidIngredients()
    {
        return fluidIngredients;
    }

    public LimaSizedItemIngredient getItemIngredient(int index)
    {
        Preconditions.checkElementIndex(index, itemIngredients.size(), "Item Ingredient");
        return itemIngredients.get(index);
    }

    public LimaSizedFluidIngredient getFluidIngredient(int index)
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
        return itemResults.stream().map(ItemResult::getMaxStack).collect(LimaStreamsUtil.toObjectList());
    }

    public List<FluidResult> getFluidResults()
    {
        return fluidResults;
    }

    public FluidResult getFluidResult(int index)
    {
        Preconditions.checkElementIndex(index, fluidResults.size(), "Fluid results");
        return fluidResults.get(index);
    }

    /**
     * Convenience accessor for the first {@link FluidStack} of this recipe's fluid results. For use in single output
     * recipes.
     * @return The first fluid result.
     */
    public FluidResult getFirstFluidResult()
    {
        Preconditions.checkState(!fluidResults.isEmpty(), "Recipe has no fluid results.");
        return fluidResults.getFirst();
    }

    public List<FluidStack> generateFluidResults(T input, HolderLookup.Provider registries, RandomSource random)
    {
        return fluidResults.stream().map(r -> r.generateResult(random)).filter(s -> !s.isEmpty()).collect(LimaStreamsUtil.toObjectList());
    }

    public List<FluidStack> getPossibleFluidResults()
    {
        return fluidResults.stream().map(FluidResult::getMaxStack).collect(LimaStreamsUtil.toObjectList());
    }
    //#endregion

    private boolean skipIngredient(LimaSizedIngredient<?, ?> ingredient, RandomSource random)
    {
        float chance = ingredient.getConsumeChance();
        return ingredient.isDeterministic() && (chance == 0 || !(random.nextFloat() < chance));
    }

    public void consumeItemIngredients(T input, RandomSource random)
    {
        ResourceHandler<ItemResource> items = input.items();
        if (itemIngredients.isEmpty() || items == null) return;

        try (Transaction tx = Transaction.openRoot())
        {
            for (LimaSizedItemIngredient ingredient : itemIngredients)
            {
                if (skipIngredient(ingredient, random)) continue;

                Ingredient root = ingredient.getIngredient();
                extractIngredient(items, (resource, count) -> root.test(resource.toStack(count)), ingredient.getSize(), tx);
            }

            tx.commit();
        }
    }

    public void consumeFluidIngredients(T input, RandomSource random)
    {
        ResourceHandler<FluidResource> fluids = input.fluids();
        if (fluidIngredients.isEmpty() || fluids == null) return;

        try (Transaction tx = Transaction.openRoot())
        {
            for (LimaSizedFluidIngredient ingredient : fluidIngredients)
            {
                if (skipIngredient(ingredient, random)) continue;

                FluidIngredient root = ingredient.getIngredient();
                extractIngredient(fluids, (resource, count) -> root.test(resource.toStack(count)), ingredient.getSize(), tx);
            }

            tx.commit();
        }
    }

    private boolean checkItemInputs(T input)
    {
        if (itemIngredients.isEmpty()) return true;

        ResourceHandler<ItemResource> items = input.items();
        if (invalidInputSize(items, itemIngredients)) return false;

        try (Transaction tx = Transaction.openRoot())
        {
            for (LimaSizedItemIngredient ingredient : itemIngredients)
            {
                Ingredient root = ingredient.getIngredient();
                boolean pass = extractIngredient(items, (resource, count) -> root.test(resource.toStack(count)), ingredient.getSize(), tx);
                if (!pass) return false;
            }
        }

        return true;
    }

    private boolean checkFluidInputs(T input)
    {
        if (fluidIngredients.isEmpty()) return true;

        ResourceHandler<FluidResource> fluids = input.fluids();
        if (invalidInputSize(fluids, fluidIngredients)) return false;

        try (Transaction tx = Transaction.openRoot())
        {
            for (LimaSizedFluidIngredient ingredient : fluidIngredients)
            {
                FluidIngredient root = ingredient.getIngredient();
                boolean pass = extractIngredient(fluids, (resource, count) -> root.test(resource.toStack(count)), ingredient.getSize(), tx);
                if (!pass) return false;
            }
        }

        return true;
    }

    @Contract("null,_->true")
    private <R extends Resource> boolean invalidInputSize(@Nullable ResourceHandler<R> handler, List<?> ingredients)
    {
        if (handler == null) return true;
        return ingredients.size() > handler.size() || ResourceHandlerUtil.isEmpty(handler);
    }

    private <R extends Resource> boolean extractIngredient(ResourceHandler<R> handler, BiPredicate<R, Integer> predicate, int amount, TransactionContext transaction)
    {
        if (amount == 0) return true;

        int remaining = amount;

        for (int index = 0; index < handler.size(); index++)
        {
            R resource = handler.getResource(index);
            if (resource.isEmpty() || !predicate.test(resource, handler.getAmountAsInt(index))) continue;

            remaining -= handler.extract(index, resource, remaining, transaction);
        }

        return remaining <= 0;
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
    public boolean showNotification()
    {
        return false;
    }

    @Deprecated
    @Override
    public PlacementInfo placementInfo()
    {
        return PlacementInfo.NOT_PLACEABLE;
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

    @FunctionalInterface
    public interface RecipeFactory<R extends LimaCustomRecipe<?>> extends Function4<List<LimaSizedItemIngredient>, List<LimaSizedFluidIngredient>, List<ItemResult>, List<FluidResult>, R> { }
}