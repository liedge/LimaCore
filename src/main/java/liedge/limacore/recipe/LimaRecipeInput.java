package liedge.limacore.recipe;

import liedge.limacore.capability.fluid.LimaFluidHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;

public interface LimaRecipeInput extends RecipeInput
{
    static LimaRecipeInput of(IItemHandler itemContainer)
    {
        return new ItemsOnly(itemContainer);
    }

    static LimaRecipeInput of(LimaFluidHandler fluidContainer)
    {
        return new FluidsOnly(fluidContainer);
    }

    static LimaRecipeInput of(IItemHandler itemContainer, LimaFluidHandler fluidContainer)
    {
        return new ItemsAndFluids(itemContainer, fluidContainer);
    }

    ItemStack extractItem(int slot, int count, boolean simulate);

    FluidStack extractFluid(int tank, int amount, IFluidHandler.FluidAction action);

    FluidStack getFluid(int tank);

    int tanks();

    default boolean isTanksEmpty()
    {
        for (int i = 0; i < tanks(); i++)
        {
            if (!getFluid(i).isEmpty()) return false;
        }

        return true;
    }

    default boolean checkItemInputSize(List<SizedIngredient> itemIngredients)
    {
        return itemIngredients.isEmpty() || (itemIngredients.size() <= size() && !isEmpty());
    }

    default boolean checkFluidInputSize(List<SizedFluidIngredient> fluidIngredients)
    {
        return fluidIngredients.isEmpty() || (fluidIngredients.size() <= tanks() && !isTanksEmpty());
    }

    interface ItemContainerSource extends LimaRecipeInput
    {
        IItemHandler itemContainer();

        @Override
        default ItemStack extractItem(int slot, int count, boolean simulate)
        {
            return itemContainer().extractItem(slot, count, simulate);
        }

        @Override
        default ItemStack getItem(int index)
        {
            return itemContainer().getStackInSlot(index);
        }

        @Override
        default int size()
        {
            return itemContainer().getSlots();
        }
    }

    interface FluidContainerSource extends LimaRecipeInput
    {
        LimaFluidHandler fluidContainer();

        @Override
        default FluidStack extractFluid(int tank, int amount, IFluidHandler.FluidAction action)
        {
            return fluidContainer().drainTank(tank, amount, action, true);
        }

        @Override
        default FluidStack getFluid(int tank)
        {
            return fluidContainer().getFluidInTank(tank);
        }

        @Override
        default int tanks()
        {
            return fluidContainer().getTanks();
        }
    }

    record ItemsOnly(IItemHandler itemContainer) implements ItemContainerSource
    {
        @Override
        public FluidStack extractFluid(int tank, int amount, IFluidHandler.FluidAction action)
        {
            return FluidStack.EMPTY;
        }

        @Override
        public FluidStack getFluid(int tank)
        {
            return FluidStack.EMPTY;
        }

        @Override
        public int tanks()
        {
            return 0;
        }
    }

    record FluidsOnly(LimaFluidHandler fluidContainer) implements FluidContainerSource
    {
        @Override
        public ItemStack extractItem(int slot, int count, boolean simulate)
        {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack getItem(int index)
        {
            return ItemStack.EMPTY;
        }

        @Override
        public int size()
        {
            return 0;
        }
    }

    record ItemsAndFluids(IItemHandler itemContainer, LimaFluidHandler fluidContainer) implements ItemContainerSource, FluidContainerSource {}
}