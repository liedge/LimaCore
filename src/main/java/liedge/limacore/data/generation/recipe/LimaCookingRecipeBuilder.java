package liedge.limacore.data.generation.recipe;

import com.google.common.base.Preconditions;
import liedge.limacore.lib.ModResources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class LimaCookingRecipeBuilder extends LimaCustomRecipeBuilder<AbstractCookingRecipe, LimaCookingRecipeBuilder>
{
    private float experience;
    private final int cookingTime;
    private final AbstractCookingRecipe.Factory<?> factory;

    public LimaCookingRecipeBuilder(RecipeSerializer<? extends AbstractCookingRecipe> serializer, ModResources resources, ItemStack result, int cookingTime, AbstractCookingRecipe.Factory<?> factory)
    {
        super(serializer, resources, result);
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
        return factory.create("", CookingBookCategory.MISC, ingredients.getFirst(), result, experience, cookingTime);
    }
}