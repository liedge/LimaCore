package liedge.limacore.data.generation.recipe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.ModResources;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.ingredient.LimaSizedFluidIngredient;
import liedge.limacore.recipe.ingredient.LimaSizedItemIngredient;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import liedge.limacore.recipe.result.ResultCount;
import liedge.limacore.recipe.result.ResultPriority;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.List;

public abstract class LimaCustomRecipeBuilder<R extends LimaCustomRecipe<?>, B extends LimaCustomRecipeBuilder<R, B>> extends LimaRecipeBuilder<R, B>
{
    public static <R extends LimaCustomRecipe<?>, B extends LimaCustomRecipeBuilder<R, B>> LimaCustomRecipeBuilder<R, B> simpleBuilder(ModResources resources, LimaCustomRecipe.RecipeFactory<R> factory)
    {
        return new SimpleBuilder<>(resources, factory);
    }

    protected final List<LimaSizedItemIngredient> itemIngredients = new ObjectArrayList<>();
    protected final List<LimaSizedFluidIngredient> fluidIngredients = new ObjectArrayList<>();
    protected final List<ItemResult> itemResults = new ObjectArrayList<>();
    protected final List<FluidResult> fluidResults = new ObjectArrayList<>();

    protected LimaCustomRecipeBuilder(ModResources modResources)
    {
        super(modResources);
    }

    public B input(LimaSizedItemIngredient sizedIngredient)
    {
        itemIngredients.add(sizedIngredient);
        return selfUnchecked();
    }

    public B input(Ingredient ingredient)
    {
        return input(new LimaSizedItemIngredient(ingredient, 1));
    }

    public B randomInput(Ingredient ingredient, float consumeChance)
    {
        return input(new LimaSizedItemIngredient(ingredient, 1, consumeChance));
    }

    public B input(Ingredient ingredient, int count)
    {
        return input(new LimaSizedItemIngredient(ingredient, count));
    }

    public B randomInput(Ingredient ingredient, int count, float consumeChance)
    {
        return input(new LimaSizedItemIngredient(ingredient, count, consumeChance));
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

    public B input(TagKey<Item> tagKey)
    {
        return input(Ingredient.of(tagKey));
    }

    public B input(TagKey<Item> tagKey, int count)
    {
        return input(Ingredient.of(tagKey), count);
    }

    public B randomInput(TagKey<Item> tagKey, int count, float consumeChance)
    {
        return randomInput(Ingredient.of(tagKey), count, consumeChance);
    }

    public B fluidInput(LimaSizedFluidIngredient ingredient)
    {
        fluidIngredients.add(ingredient);
        return selfUnchecked();
    }

    public B fluidInput(FluidIngredient ingredient, int amount)
    {
        return fluidInput(new LimaSizedFluidIngredient(ingredient, amount));
    }

    public B randomFluidInput(FluidIngredient ingredient, int amount, float consumeChance)
    {
        return fluidInput(new LimaSizedFluidIngredient(ingredient, amount, consumeChance));
    }

    public B fluidInput(FluidStack fluidStack)
    {
        return fluidInput(FluidIngredient.single(fluidStack), fluidStack.getAmount());
    }

    public B randomFluidInput(FluidStack fluidStack, float consumeChance)
    {
        return randomFluidInput(FluidIngredient.single(fluidStack), fluidStack.getAmount(), consumeChance);
    }

    public B fluidInput(Fluid fluid, int amount)
    {
        return fluidInput(FluidIngredient.single(fluid), amount);
    }

    public B randomFluidInput(Fluid fluid, int amount, float consumeChance)
    {
        return randomFluidInput(FluidIngredient.single(fluid), amount, consumeChance);
    }

    public B fluidInput(Holder<Fluid> fluidHolder, int amount)
    {
        return fluidInput(fluidHolder.value(), amount);
    }

    public B randomFluidInput(Holder<Fluid> fluidHolder, int amount, float consumeChance)
    {
        return randomFluidInput(fluidHolder.value(), amount, consumeChance);
    }

    public B fluidInput(TagKey<Fluid> tagKey, int amount)
    {
        return fluidInput(FluidIngredient.tag(tagKey), amount);
    }

    public B randomFluidInput(TagKey<Fluid> tagKey, int amount, float consumeChance)
    {
        return randomFluidInput(FluidIngredient.tag(tagKey), amount, consumeChance);
    }

    //#region Item results
    public B output(ItemResult result)
    {
        itemResults.add(result);
        return selfUnchecked();
    }

    public B output(ItemStack stack, float chance, ResultPriority priority)
    {
        return output(ItemResult.create(stack, chance, priority));
    }

    public B output(ItemStack stack, float chance)
    {
        return output(stack, chance, ResultPriority.PRIMARY);
    }

    public B output(ItemStack stack)
    {
        return output(stack, 1f);
    }

    public B output(ItemLike itemLike, ResultCount count, float chance, ResultPriority priority)
    {
        return output(ItemResult.create(itemLike, null, count, chance, priority));
    }

    public B output(ItemLike itemLike, ResultCount count, float chance)
    {
        return output(itemLike, count, chance, ResultPriority.PRIMARY);
    }

    public B output(ItemLike itemLike, ResultCount count)
    {
        return output(itemLike, count, 1f);
    }

    public B output(ItemLike itemLike, int count)
    {
        return output(new ItemStack(itemLike, count));
    }

    public B output(ItemLike itemLike)
    {
        return output(itemLike, 1);
    }
    //#endregion

    //#region Fluid results
    public B fluidOutput(FluidResult result)
    {
        fluidResults.add(result);
        return selfUnchecked();
    }

    public B fluidOutput(FluidStack stack, float chance, ResultPriority priority)
    {
        return fluidOutput(FluidResult.create(stack, chance, priority));
    }

    public B fluidOutput(FluidStack stack, float chance)
    {
        return fluidOutput(stack, chance, ResultPriority.PRIMARY);
    }

    public B fluidOutput(FluidStack fluidStack)
    {
        return fluidOutput(fluidStack, 1f);
    }

    public B fluidOutput(Fluid fluid, ResultCount count, float chance, ResultPriority priority)
    {
        return fluidOutput(FluidResult.create(fluid, null, count, chance, priority));
    }

    public B fluidOutput(Holder<Fluid> fluidHolder, ResultCount count, float chance, ResultPriority priority)
    {
        return fluidOutput(fluidHolder.value(), count, chance, priority);
    }

    public B fluidOutput(Fluid fluid, ResultCount count, float chance)
    {
        return fluidOutput(fluid, count, chance, ResultPriority.PRIMARY);
    }

    public B fluidOutput(Holder<Fluid> fluidHolder, ResultCount count, float chance)
    {
        return fluidOutput(fluidHolder.value(), count, chance);
    }

    public B fluidOutput(Fluid fluid, ResultCount count)
    {
        return fluidOutput(fluid, count, 1f);
    }

    public B fluidOutput(Holder<Fluid> fluidHolder, ResultCount count)
    {
        return fluidOutput(fluidHolder.value(), count);
    }

    public B fluidOutput(Fluid fluid, int amount)
    {
        return fluidOutput(new FluidStack(fluid, amount));
    }

    public B fluidOutput(Holder<Fluid> fluidHolder, int amount)
    {
        return fluidOutput(fluidHolder.value(), amount);
    }
    //#endregion

    @Override
    protected String getDefaultRecipeName()
    {
        if (!itemResults.isEmpty())
            return LimaRegistryUtil.getItemName(itemResults.getFirst().getItem());
        else if (!fluidResults.isEmpty())
            return LimaRegistryUtil.getFluidName(fluidResults.getFirst().getFluid());
        else
            throw new IllegalStateException("Default recipe name cannot be determined without any item or fluid results.");
    }

    private static class SimpleBuilder<R extends LimaCustomRecipe<?>, B extends LimaCustomRecipeBuilder<R, B>> extends LimaCustomRecipeBuilder<R, B>
    {
        private final LimaCustomRecipe.RecipeFactory<R> factory;

        private SimpleBuilder(ModResources modResources, LimaCustomRecipe.RecipeFactory<R> factory)
        {
            super(modResources);
            this.factory = factory;
        }

        @Override
        protected R buildRecipe()
        {
            return factory.apply(itemIngredients, fluidIngredients, itemResults, fluidResults);
        }
    }
}