package liedge.limacore.data.generation.recipe;

import com.google.common.base.Preconditions;
import liedge.limacore.lib.ModResources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;

public class LimaCookingRecipeBuilder extends LimaSimpleRecipeBuilder<AbstractCookingRecipe, LimaCookingRecipeBuilder>
{
    private float experience;
    private final int cookingTime;
    private final AbstractCookingRecipe.Factory<?> factory;

    public LimaCookingRecipeBuilder(ModResources resources, ItemStack resultItem, int cookingTime, AbstractCookingRecipe.Factory<?> factory)
    {
        super(resources, resultItem);
        this.cookingTime = cookingTime;
        this.factory = factory;
    }

    public LimaCookingRecipeBuilder xp(float experience)
    {
        this.experience = experience;
        return this;
    }

    @Override
    protected void validate(ResourceLocation id)
    {
        Preconditions.checkState(ingredients.size() == 1, "Cooking recipe '" + id + "' must have exactly 1 ingredient.");
    }

    @Override
    protected AbstractCookingRecipe buildRecipe()
    {
        return factory.create("", CookingBookCategory.MISC, ingredients.getFirst(), resultItem, experience, cookingTime);
    }
}