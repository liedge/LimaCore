package liedge.limacore.data.generation.recipe;

import liedge.limacore.lib.ModResources;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.function.BiFunction;

public abstract class LimaSimpleRecipeBuilder<R extends Recipe<?>, B extends LimaSimpleRecipeBuilder<R, B>> extends LimaCustomRecipeBuilder<R, B>
{
    public static <R extends Recipe<?>, B extends LimaSimpleRecipeBuilder<R, B>> LimaSimpleRecipeBuilder<R, B> simpleBuilder(ModResources resources, ItemStack resultItem, BiFunction<NonNullList<Ingredient>, ItemStack, R> factory)
    {
        return new LimaSimpleRecipeBuilder<>(resources, resultItem)
        {
            @Override
            protected R buildRecipe()
            {
                return factory.apply(buildIngredients(), this.resultItem);
            }
        };
    }

    protected final ItemStack resultItem;

    public LimaSimpleRecipeBuilder(ModResources modResources, ItemStack resultItem)
    {
        super(modResources);
        this.resultItem = resultItem;
    }

    @Override
    protected void validate(ResourceLocation id) {}

    @Override
    protected String getDefaultRecipeName()
    {
        return getDefaultStackName(resultItem);
    }
}