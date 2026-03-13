package liedge.limacore.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

public interface LimaRecipeInput extends RecipeInput
{
    @Nullable ResourceHandler<ItemResource> items();

    @Nullable ResourceHandler<FluidResource> fluids();

    @Override
    default int size()
    {
        return handlerSize(items());
    }

    default int fluidsSize()
    {
        return handlerSize(fluids());
    }

    @Override
    default ItemStack getItem(int index)
    {
        ResourceHandler<ItemResource> items = items();
        if (items != null)
        {
            return items.getResource(index).toStack(items.getAmountAsInt(index));
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

    default FluidStack getFluid(int index)
    {
        ResourceHandler<FluidResource> fluids = fluids();
        if (fluids != null)
        {
            return fluids.getResource(index).toStack(fluids.getAmountAsInt(index));
        }
        else
        {
            return FluidStack.EMPTY;
        }
    }

    @Override
    default boolean isEmpty()
    {
        return areItemsEmpty() && areFluidsEmpty();
    }

    default boolean areItemsEmpty()
    {
        ResourceHandler<ItemResource> items = items();
        return items == null || ResourceHandlerUtil.isEmpty(items);
    }

    default boolean areFluidsEmpty()
    {
        ResourceHandler<FluidResource> fluids = fluids();
        return fluids == null || ResourceHandlerUtil.isEmpty(fluids);
    }

    private int handlerSize(@Nullable ResourceHandler<?> handler)
    {
        return handler != null ? handler.size() : 0;
    }
}