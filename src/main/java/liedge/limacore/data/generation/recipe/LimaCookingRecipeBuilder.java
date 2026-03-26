package liedge.limacore.data.generation.recipe;

import com.google.common.base.Preconditions;
import liedge.limacore.lib.ModResources;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Recipe;

public class LimaCookingRecipeBuilder extends VanillaRecipeBuilder.StandardIngredients<AbstractCookingRecipe, CookingBookCategory, AbstractCookingRecipe.CookingBookInfo, LimaCookingRecipeBuilder>
{
    private final int cookingTime;
    private final AbstractCookingRecipe.Factory<?> factory;
    private float experience;

    public LimaCookingRecipeBuilder(ModResources resources, ItemStackTemplate result, int cookingTime, AbstractCookingRecipe.Factory<?> factory)
    {
        super(resources, result, CookingBookCategory.MISC, AbstractCookingRecipe.CookingBookInfo::new);
        this.cookingTime = cookingTime;
        this.factory = factory;
    }

    public LimaCookingRecipeBuilder xp(float experience)
    {
        this.experience = experience;
        return this;
    }

    @Override
    protected AbstractCookingRecipe buildRecipe(Recipe.CommonInfo commonInfo, AbstractCookingRecipe.CookingBookInfo categoryInfo, ItemStackTemplate result)
    {
        Preconditions.checkState(ingredients.size() == 1, "Cooking recipe must have exactly 1 ingredient.");
        return factory.create(commonInfo, categoryInfo, ingredients.getFirst(), result, experience, cookingTime);
    }
}