package liedge.limacore.data.generation.recipe;

import liedge.limacore.lib.ModResources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Objects;

public class LimaCookingRecipeBuilder extends SingleResultRecipeBuilder<AbstractCookingRecipe, LimaCookingRecipeBuilder>
{
    private Ingredient ingredient;
    private float experience;
    private final int cookingTime;
    private final AbstractCookingRecipe.Factory<?> factory;

    public LimaCookingRecipeBuilder(RecipeSerializer<? extends AbstractCookingRecipe> serializer, ModResources resources, ItemStack result, int cookingTime, AbstractCookingRecipe.Factory<?> factory)
    {
        super(serializer, resources, result);
        this.cookingTime = cookingTime;
        this.factory = factory;
    }

    public LimaCookingRecipeBuilder input(Ingredient ingredient)
    {
        this.ingredient = ingredient;
        return this;
    }

    public LimaCookingRecipeBuilder xp(float experience)
    {
        this.experience = experience;
        return this;
    }

    @Override
    protected void validate(ResourceLocation id)
    {
        Objects.requireNonNull(ingredient, "Missing ingredient for recipe " + id);
    }

    @Override
    protected AbstractCookingRecipe buildRecipe()
    {
        return factory.create("", CookingBookCategory.MISC, ingredient, result, experience, cookingTime);
    }
}