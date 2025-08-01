package liedge.limacore.data.generation.recipe;

import com.google.common.base.Preconditions;
import liedge.limacore.lib.ModResources;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;

public class LimaCookingRecipeBuilder extends StandardRecipeBuilder<AbstractCookingRecipe, LimaCookingRecipeBuilder>
{
    private final int cookingTime;
    private final AbstractCookingRecipe.Factory<?> factory;
    private float experience;
    private CookingBookCategory category = CookingBookCategory.MISC;

    public LimaCookingRecipeBuilder(ModResources resources, ItemStack result, int cookingTime, AbstractCookingRecipe.Factory<?> factory)
    {
        super(resources, result);
        this.cookingTime = cookingTime;
        this.factory = factory;
    }

    public LimaCookingRecipeBuilder xp(float experience)
    {
        this.experience = experience;
        return this;
    }

    public LimaCookingRecipeBuilder bookCategory(CookingBookCategory category)
    {
        this.category = category;
        return this;
    }

    @Override
    protected AbstractCookingRecipe buildRecipe()
    {
        Preconditions.checkState(ingredients.size() == 1, "Cooking recipe must have exactly 1 ingredient.");
        return factory.create(getGroupOrBlank(), category, ingredients.getFirst(), result, experience, cookingTime);
    }
}