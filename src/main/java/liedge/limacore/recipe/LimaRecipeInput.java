package liedge.limacore.recipe;

import liedge.limacore.capability.fluid.LimaFluidHandler;
import liedge.limacore.recipe.ingredient.LimaSizedFluidIngredient;
import liedge.limacore.recipe.ingredient.LimaSizedItemIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LimaRecipeInput extends RecipeInput
{
    static LimaRecipeInput create(@Nullable IItemHandler itemContainer, @Nullable LimaFluidHandler fluidContainer)
    {
        return new SimpleWrapper(itemContainer, fluidContainer);
    }

    ItemStack extractItem(int slot, int count, boolean simulate);

    FluidStack extractFluid(int tank, int amount, IFluidHandler.FluidAction action);

    FluidStack getFluid(int tank);

    int tanks();

    @Override
    default boolean isEmpty()
    {
        return areItemsEmpty() && areFluidsEmpty();
    }

    default boolean areItemsEmpty()
    {
        return RecipeInput.super.isEmpty();
    }

    default boolean areFluidsEmpty()
    {
        for (int i = 0; i < tanks(); i++)
        {
            if (!getFluid(i).isEmpty()) return false;
        }

        return true;
    }

    default boolean checkItemInputSize(List<LimaSizedItemIngredient> itemIngredients)
    {
        return itemIngredients.isEmpty() || (itemIngredients.size() <= size() && !areItemsEmpty());
    }

    default boolean checkFluidInputSize(List<LimaSizedFluidIngredient> fluidIngredients)
    {
        return fluidIngredients.isEmpty() || (fluidIngredients.size() <= tanks() && !areFluidsEmpty());
    }

    interface ContainerWrapper extends LimaRecipeInput
    {
        @Nullable IItemHandler itemContainer();

        @Nullable LimaFluidHandler fluidContainer();

        @Override
        default ItemStack extractItem(int slot, int count, boolean simulate)
        {
            IItemHandler container = itemContainer();
            return container != null ? container.extractItem(slot, count, simulate) : ItemStack.EMPTY;
        }

        @Override
        default ItemStack getItem(int index)
        {
            IItemHandler container = itemContainer();
            return container != null ? container.getStackInSlot(index) : ItemStack.EMPTY;
        }

        @Override
        default int size()
        {
            IItemHandler container = itemContainer();
            return container != null ? container.getSlots() : 0;
        }

        @Override
        default FluidStack extractFluid(int tank, int amount, IFluidHandler.FluidAction action)
        {
            LimaFluidHandler container = fluidContainer();
            return container != null ? container.drainTank(tank, amount, action, true) : FluidStack.EMPTY;
        }

        @Override
        default FluidStack getFluid(int tank)
        {
            LimaFluidHandler container = fluidContainer();
            return container != null ? container.getFluidInTank(tank) : FluidStack.EMPTY;
        }

        @Override
        default int tanks()
        {
            LimaFluidHandler container = fluidContainer();
            return container != null ? container.getTanks() : 0;
        }
    }

    record SimpleWrapper(@Nullable IItemHandler itemContainer, @Nullable LimaFluidHandler fluidContainer) implements ContainerWrapper { }
}