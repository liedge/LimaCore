package liedge.limacore.data.generation.recipe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.ModResources;
import liedge.limacore.recipe.ItemResult;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;
import java.util.function.BiFunction;

public abstract class LimaCustomRecipeBuilder<R extends LimaCustomRecipe<?>, B extends LimaCustomRecipeBuilder<R, B>> extends LimaRecipeBuilder<R, B>
{
    public static <R extends LimaCustomRecipe<?>, B extends LimaCustomRecipeBuilder<R, B>> LimaCustomRecipeBuilder<R, B> simpleBuilder(ModResources resources, RecipeConstructor<R> factory)
    {
        return new SimpleBuilder<>(resources, factory);
    }

    public static <R extends LimaCustomRecipe<?>, B extends LimaCustomRecipeBuilder<R, B>> LimaCustomRecipeBuilder<R, B> simpleBuilder(ModResources resources, BiFunction<List<SizedIngredient>, List<ItemResult>, R> factory)
    {
        return simpleBuilder(resources, (itemIngredients, p2, itemResults, p4) -> factory.apply(itemIngredients, itemResults));
    }

    protected final List<SizedIngredient> itemIngredients = new ObjectArrayList<>();
    protected final List<SizedFluidIngredient> fluidIngredients = new ObjectArrayList<>();
    protected final List<ItemResult> itemResults = new ObjectArrayList<>();
    protected final List<FluidStack> fluidResults = new ObjectArrayList<>();

    protected LimaCustomRecipeBuilder(ModResources modResources)
    {
        super(modResources);
    }

    public B input(SizedIngredient sizedIngredient)
    {
        itemIngredients.add(sizedIngredient);
        return selfUnchecked();
    }

    public B input(Ingredient ingredient)
    {
        return input(new SizedIngredient(ingredient, 1));
    }

    public B input(Ingredient ingredient, int count)
    {
        return input(new SizedIngredient(ingredient, count));
    }

    public B input(ItemLike itemLike)
    {
        return input(Ingredient.of(itemLike));
    }

    public B input(ItemLike itemLike, int count)
    {
        return input(SizedIngredient.of(itemLike, count));
    }

    public B input(TagKey<Item> tagKey)
    {
        return input(Ingredient.of(tagKey));
    }

    public B input(TagKey<Item> tagKey, int count)
    {
        return input(SizedIngredient.of(tagKey, count));
    }

    public B fluidInput(SizedFluidIngredient ingredient)
    {
        fluidIngredients.add(ingredient);
        return selfUnchecked();
    }

    public B fluidInput(FluidStack fluidStack)
    {
        return fluidInput(SizedFluidIngredient.of(fluidStack));
    }

    public B fluidInput(Fluid fluid, int amount)
    {
        return fluidInput(SizedFluidIngredient.of(fluid, amount));
    }

    public B fluidInput(Holder<Fluid> fluidHolder, int amount)
    {
        return fluidInput(fluidHolder.value(), amount);
    }

    public B fluidInput(TagKey<Fluid> tagKey, int amount)
    {
        return fluidInput(SizedFluidIngredient.of(tagKey, amount));
    }

    public B output(ItemResult result)
    {
        itemResults.add(result);
        return selfUnchecked();
    }

    public B output(ItemStack stack, float chance)
    {
        return output(new ItemResult(stack, chance));
    }

    public B output(ItemStack stack)
    {
        return output(new ItemResult(stack));
    }

    public B output(ItemLike itemLike, int count, float chance)
    {
        return output(new ItemStack(itemLike, count), chance);
    }

    public B output(ItemLike itemLike, int count)
    {
        return output(new ItemStack(itemLike, count));
    }

    public B output(ItemLike itemLike)
    {
        return output(itemLike, 1);
    }

    public B fluidOutput(FluidStack fluidStack)
    {
        fluidResults.add(fluidStack);
        return selfUnchecked();
    }

    public B fluidOutput(Fluid fluid, int amount)
    {
        return fluidOutput(new FluidStack(fluid, amount));
    }

    public B fluidOutput(Holder<Fluid> fluidHolder, int amount)
    {
        return fluidOutput(new FluidStack(fluidHolder.value(), amount));
    }

    @Override
    protected String getDefaultRecipeName()
    {
        return LimaRegistryUtil.getItemName(itemResults.getFirst().item());
    }

    private static class SimpleBuilder<R extends LimaCustomRecipe<?>, B extends LimaCustomRecipeBuilder<R, B>> extends LimaCustomRecipeBuilder<R, B>
    {
        private final RecipeConstructor<R> factory;

        private SimpleBuilder(ModResources modResources, RecipeConstructor<R> factory)
        {
            super(modResources);
            this.factory = factory;
        }

        @Override
        protected R buildRecipe()
        {
            return factory.create(itemIngredients, fluidIngredients, itemResults, fluidResults);
        }
    }

    @FunctionalInterface
    public interface RecipeConstructor<R extends LimaCustomRecipe<?>>
    {
        R create(List<SizedIngredient> itemIngredients, List<SizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, List<FluidStack> fluidResults);
    }
}