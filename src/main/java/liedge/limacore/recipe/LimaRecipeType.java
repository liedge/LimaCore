package liedge.limacore.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

public record LimaRecipeType<R extends LimaCustomRecipe<?>>(ResourceLocation id) implements RecipeType<R>
{
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        else if (obj instanceof LimaRecipeType<?> other)
        {
            return id.equals(other.id);
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    public String toString()
    {
        return id.toString();
    }
}