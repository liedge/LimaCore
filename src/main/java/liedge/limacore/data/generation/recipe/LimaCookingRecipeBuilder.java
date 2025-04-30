package liedge.limacore.data.generation.recipe;

import com.google.common.base.Preconditions;
import liedge.limacore.lib.ModResources;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Objects;

public class LimaCookingRecipeBuilder extends LimaIngredientsRecipeBuilder<AbstractCookingRecipe, LimaCookingRecipeBuilder>
{
    private final ItemStack result;
    private final int cookingTime;
    private final AbstractCookingRecipe.Factory<?> factory;
    private Ingredient ingredient;
    private float experience;
    private CookingBookCategory category = CookingBookCategory.MISC;

    public LimaCookingRecipeBuilder(ModResources resources, ItemStack result, int cookingTime, AbstractCookingRecipe.Factory<?> factory)
    {
        super(resources);
        this.result = result;
        this.cookingTime = cookingTime;
        this.factory = factory;
    }

    public LimaCookingRecipeBuilder xp(float experience)
    {
        this.experience = experience;
        return this;
    }

    public LimaCookingRecipeBuilder cookCategory(CookingBookCategory category)
    {
        this.category = category;
        return this;
    }

    @Override
    public LimaCookingRecipeBuilder input(Ingredient ingredient)
    {
        Preconditions.checkState(this.ingredient == null, "Cooking recipe already has an ingredient.");
        this.ingredient = ingredient;
        return this;
    }

    @Override
    protected AbstractCookingRecipe buildRecipe()
    {
        return factory.create(getGroupOrBlank(), category, Objects.requireNonNull(ingredient, "No ingredient for cooking recipe."), result, experience, cookingTime);
    }

    @Override
    protected String getDefaultRecipeName()
    {
        return getDefaultStackName(result);
    }
}