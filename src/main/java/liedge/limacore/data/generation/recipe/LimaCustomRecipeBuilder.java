package liedge.limacore.data.generation.recipe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.ModResources;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.input.RecipeFluidInput;
import liedge.limacore.recipe.input.RecipeItemInput;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.List;

public abstract class LimaCustomRecipeBuilder<R extends LimaCustomRecipe<?>, B extends LimaCustomRecipeBuilder<R, B>> extends LimaRecipeBuilder<R, B>
{
    protected final List<RecipeItemInput> itemInputs = new ObjectArrayList<>();
    protected final List<RecipeFluidInput> fluidInputs = new ObjectArrayList<>();
    protected final List<ItemResult> itemResults = new ObjectArrayList<>();
    protected final List<FluidResult> fluidResults = new ObjectArrayList<>();

    protected LimaCustomRecipeBuilder(ModResources modResources)
    {
        super(modResources);
    }

    public B input(RecipeItemInput itemInput)
    {
        itemInputs.add(itemInput);
        return selfUnchecked();
    }

    public B input(Ingredient ingredient)
    {
        return input(new RecipeItemInput(ingredient, 1, 1));
    }

    public B randomInput(Ingredient ingredient, float consumeChance)
    {
        return input(new RecipeItemInput(ingredient, 1, consumeChance));
    }

    public B input(Ingredient ingredient, int count)
    {
        return input(new RecipeItemInput(ingredient, count, 1));
    }

    public B randomInput(Ingredient ingredient, int count, float consumeChance)
    {
        return input(new RecipeItemInput(ingredient, count, consumeChance));
    }

    public B input(ItemLike itemLike)
    {
        return input(Ingredient.of(itemLike));
    }

    public B input(ItemLike itemLike, int count)
    {
        return input(Ingredient.of(itemLike), count);
    }

    public B randomInput(ItemLike itemLike, int count, float consumeChance)
    {
        return randomInput(Ingredient.of(itemLike), count, consumeChance);
    }

    public B input(HolderGetter<Item> holders, TagKey<Item> tagKey)
    {
        return input(holders, tagKey, 1);
    }

    public B input(HolderGetter<Item> holders, TagKey<Item> tagKey, int count)
    {
        return input(Ingredient.of(holders.getOrThrow(tagKey)), count);
    }

    public B randomInput(HolderGetter<Item> holders, TagKey<Item> tagKey, int count, float consumeChance)
    {
        return randomInput(Ingredient.of(holders.getOrThrow(tagKey)), count, consumeChance);
    }

    public B fluidInput(RecipeFluidInput fluidInput)
    {
        fluidInputs.add(fluidInput);
        return selfUnchecked();
    }

    public B fluidInput(FluidIngredient ingredient, int amount)
    {
        return fluidInput(new RecipeFluidInput(ingredient, amount, 1));
    }

    public B randomFluidInput(FluidIngredient ingredient, int amount, float consumeChance)
    {
        return fluidInput(new RecipeFluidInput(ingredient, amount, consumeChance));
    }

    public B fluidInput(FluidStack fluidStack)
    {
        return fluidInput(FluidIngredient.of(fluidStack), fluidStack.getAmount());
    }

    public B randomFluidInput(FluidStack fluidStack, float consumeChance)
    {
        return randomFluidInput(FluidIngredient.of(fluidStack), fluidStack.getAmount(), consumeChance);
    }

    public B fluidInput(Fluid fluid, int amount)
    {
        return fluidInput(FluidIngredient.of(fluid), amount);
    }

    public B randomFluidInput(Fluid fluid, int amount, float consumeChance)
    {
        return randomFluidInput(FluidIngredient.of(fluid), amount, consumeChance);
    }

    public B fluidInput(Holder<Fluid> fluidHolder, int amount)
    {
        return fluidInput(fluidHolder.value(), amount);
    }

    public B randomFluidInput(Holder<Fluid> fluidHolder, int amount, float consumeChance)
    {
        return randomFluidInput(fluidHolder.value(), amount, consumeChance);
    }

    public B fluidInput(HolderGetter<Fluid> holders, TagKey<Fluid> tagKey, int amount)
    {
        return fluidInput(FluidIngredient.of(holders.getOrThrow(tagKey)), amount);
    }

    public B randomFluidInput(HolderGetter<Fluid> holders, TagKey<Fluid> tagKey, int amount, float consumeChance)
    {
        return randomFluidInput(FluidIngredient.of(holders.getOrThrow(tagKey)), amount, consumeChance);
    }

    //#region Results (simplified)
    public B output(ItemResult result)
    {
        itemResults.add(result);
        return selfUnchecked();
    }

    public B fluidOutput(FluidResult result)
    {
        fluidResults.add(result);
        return selfUnchecked();
    }

    //#endregion

    @Override
    protected String getDefaultRecipeName()
    {
        if (!itemResults.isEmpty())
            return LimaRegistryUtil.getNonNullRegistryId(itemResults.getFirst().item()).getPath();
        else if (!fluidResults.isEmpty())
            return LimaRegistryUtil.getNonNullRegistryId(fluidResults.getFirst().fluid()).getPath();
        else
            throw new IllegalStateException("Default recipe name cannot be determined without any item or fluid results.");
    }
}