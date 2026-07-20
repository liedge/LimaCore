package liedge.limacore.recipe;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import liedge.limacore.recipe.input.RecipeFluidInput;
import liedge.limacore.recipe.input.RecipeItemInput;
import liedge.limacore.recipe.input.RecipeStackInput;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import liedge.limacore.registry.game.LimaCoreRecipes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.BiPredicate;

public abstract class LimaCustomRecipe<T extends RecipeInputAccess> implements Recipe<T>
{
    public static final String EMPTY_GROUP = "";
    public static final MapCodec<String> GROUP_MAP_CODEC = Codec.STRING.optionalFieldOf("group", EMPTY_GROUP);

    public static <R extends LimaCustomRecipe<?>> DataResult<R> checkNotEmpty(R recipe, boolean checkInputs)
    {
        if (checkInputs && recipe.getItemInputs().isEmpty() && recipe.getFluidInputs().isEmpty())
            return DataResult.error(() -> "Recipe has no item or fluid inputs.");
        else if (recipe.getItemResults().isEmpty() && recipe.getFluidResults().isEmpty())
            return DataResult.error(() -> "Recipe has no item or fluid results.");
        else
            return DataResult.success(recipe);
    }

    public static <R extends LimaCustomRecipe<?>> DataResult<R> checkNotEmpty(R recipe)
    {
        return checkNotEmpty(recipe, true);
    }

    // Ingredients
    private final List<RecipeItemInput> itemInputs;
    private final List<RecipeFluidInput> fluidInputs;

    // Results
    private final List<ItemResult> itemResults;
    private final List<FluidResult> fluidResults;

    protected LimaCustomRecipe(List<RecipeItemInput> itemInputs, List<RecipeFluidInput> fluidInputs, List<ItemResult> itemResults, List<FluidResult> fluidResults)
    {
        this.itemInputs = itemInputs;
        this.fluidInputs = fluidInputs;
        this.itemResults = itemResults;
        this.fluidResults = fluidResults;
    }

    protected LimaCustomRecipe(List<RecipeItemInput> itemInputs, List<ItemResult> itemResults)
    {
        this(itemInputs, List.of(), itemResults, List.of());
    }

    //#region Ingredient functions
    public List<RecipeItemInput> getItemInputs()
    {
        return itemInputs;
    }

    public List<RecipeFluidInput> getFluidInputs()
    {
        return fluidInputs;
    }

    public RecipeItemInput getItemInput(int index)
    {
        Preconditions.checkElementIndex(index, itemInputs.size(), "Item Input");
        return itemInputs.get(index);
    }

    public RecipeFluidInput getFluidInput(int index)
    {
        Preconditions.checkElementIndex(index, fluidInputs.size(), "Fluid Input");
        return fluidInputs.get(index);
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

    public List<ResourceStack<ItemResource>> generateItemResults(RandomSource random)
    {
        return LimaRecipeUtil.generateResultStacks(random, itemResults);
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

    public List<ResourceStack<FluidResource>> generateFluidResults(RandomSource random)
    {
        return LimaRecipeUtil.generateResultStacks(random, fluidResults);
    }
    //#endregion

    private boolean skipInput(RecipeStackInput<?, ?> input, RandomSource random)
    {
        float chance = input.consumeChance();
        return input.isRandom() && (chance == 0 || !(random.nextFloat() < chance));
    }

    public void consumeItemInputs(T inputAccess, RandomSource random)
    {
        ResourceHandler<ItemResource> items = inputAccess.items();
        if (itemInputs.isEmpty() || items == null) return;

        try (Transaction tx = Transaction.openRoot())
        {
            for (RecipeItemInput input : itemInputs)
            {
                if (skipInput(input, random)) continue;

                Ingredient ingredient = input.ingredient();
                extractIngredient(items, (resource, count) -> ingredient.test(resource.toStack(count)), input.count(), tx);
            }

            tx.commit();
        }
    }

    public void consumeFluidInputs(T inputAccess, RandomSource random)
    {
        ResourceHandler<FluidResource> fluids = inputAccess.fluids();
        if (fluidInputs.isEmpty() || fluids == null) return;

        try (Transaction tx = Transaction.openRoot())
        {
            for (RecipeFluidInput input : fluidInputs)
            {
                if (skipInput(input, random)) continue;

                FluidIngredient ingredient = input.ingredient();
                extractIngredient(fluids, (resource, amount) -> ingredient.test(resource.toStack(amount)), input.count(), tx);
            }

            tx.commit();
        }
    }

    private boolean checkItemInputs(T inputAccess)
    {
        if (itemInputs.isEmpty()) return true;

        ResourceHandler<ItemResource> items = inputAccess.items();
        if (invalidInputSize(items, itemInputs)) return false;

        try (Transaction tx = Transaction.openRoot())
        {
            for (RecipeItemInput input : itemInputs)
            {
                Ingredient ingredient = input.ingredient();
                boolean pass = extractIngredient(items, (resource, count) -> ingredient.test(resource.toStack(count)), input.count(), tx);
                if (!pass) return false;
            }
        }

        return true;
    }

    private boolean checkFluidInputs(T inputAccess)
    {
        if (fluidInputs.isEmpty()) return true;

        ResourceHandler<FluidResource> fluids = inputAccess.fluids();
        if (invalidInputSize(fluids, fluidInputs)) return false;

        try (Transaction tx = Transaction.openRoot())
        {
            for (RecipeFluidInput input : fluidInputs)
            {
                FluidIngredient ingredient = input.ingredient();
                boolean pass = extractIngredient(fluids, (resource, count) -> ingredient.test(resource.toStack(count)), input.count(), tx);
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
    public String group()
    {
        return EMPTY_GROUP;
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

    @Deprecated
    @Override
    public RecipeBookCategory recipeBookCategory()
    {
        return LimaCoreRecipes.CUSTOM_RECIPE_CATEGORY.get();
    }

    @Deprecated
    @Override
    public List<RecipeDisplay> display()
    {
        return List.of();
    }

    /**
     * @deprecated Use {@link #generateItemResults(RandomSource)} to create
     * recipe item outputs.
     */
    @Deprecated
    @Override
    public ItemStack assemble(T input)
    {
        return ItemStack.EMPTY;
    }
}