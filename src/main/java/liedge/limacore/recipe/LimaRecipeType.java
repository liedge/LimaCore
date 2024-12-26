package liedge.limacore.recipe;

import liedge.limacore.lib.ModResources;
import liedge.limacore.lib.Translatable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * Simple implementation of {@link RecipeType} with registry id property. Also implements {@link Translatable} for use where recipe type name is needed (i.e. JEI plugins)
 */
public record LimaRecipeType<R extends Recipe<?>>(ResourceLocation id, String descriptionId) implements RecipeType<R>, Translatable
{
    public static <T extends Recipe<?>> LimaRecipeType<T> create(ResourceLocation id)
    {
        return new LimaRecipeType<>(id, ModResources.prefixIdTranslationKey("recipe_type", id));
    }

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