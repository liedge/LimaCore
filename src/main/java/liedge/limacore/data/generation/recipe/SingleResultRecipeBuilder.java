package liedge.limacore.data.generation.recipe;

import liedge.limacore.lib.ModResources;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

public abstract class SingleResultRecipeBuilder<R extends Recipe<?>, T extends SingleResultRecipeBuilder<R, T>> extends LimaRecipeBuilder<R, T>
{
    protected final ItemStack result;

    protected SingleResultRecipeBuilder(RecipeSerializer<? extends R> serializer, ModResources resources, ItemStack result)
    {
        super(serializer, resources);
        this.result = result;
    }

    protected SingleResultRecipeBuilder(Supplier<RecipeSerializer<? extends R>> supplier, ModResources resources, ItemStack result)
    {
        this(supplier.get(), resources, result);
    }

    @Override
    protected String getDefaultRecipeName()
    {
        return LimaRegistryUtil.getItemName(result.getItem());
    }
}