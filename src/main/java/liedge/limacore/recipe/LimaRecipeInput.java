package liedge.limacore.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;

public interface LimaRecipeInput extends RecipeInput
{
    static LimaRecipeInput create(IItemHandler container)
    {
        return new SimpleInput(container);
    }

    static LimaRecipeInput createRanged(IItemHandlerModifiable container, int minSlot, int maxSlotExclusive)
    {
        return new SimpleInput(new RangedWrapper(container, minSlot, maxSlotExclusive));
    }

    static LimaRecipeInput createRanged(IItemHandlerModifiable container, int maxSlotExclusive)
    {
        return createRanged(container, 0, maxSlotExclusive);
    }

    static LimaRecipeInput createWithSize(IItemHandlerModifiable container, int minSlot, int size)
    {
        return createRanged(container, minSlot, minSlot + size);
    }

    static LimaRecipeInput createWithSize(IItemHandlerModifiable container, int size)
    {
        return createWithSize(container, 0, size);
    }

    static LimaRecipeInput createSingleSlot(IItemHandlerModifiable container, int slotIndex)
    {
        return createWithSize(container, slotIndex, 1);
    }

    IItemHandler container();

    default ItemStack extractFromContainer(int index, int count, boolean simulate)
    {
        return container().extractItem(index, count, simulate);
    }

    @Override
    default ItemStack getItem(int index)
    {
        return container().getStackInSlot(index);
    }

    @Override
    default int size()
    {
        return container().getSlots();
    }

    record SimpleInput(IItemHandler container) implements LimaRecipeInput {}
}