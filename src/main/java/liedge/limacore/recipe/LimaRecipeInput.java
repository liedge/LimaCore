package liedge.limacore.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.items.IItemHandler;

public class LimaRecipeInput implements RecipeInput
{
    public static LimaRecipeInput matchingContainer(IItemHandler container)
    {
        return new LimaRecipeInput(container, container.getSlots(), 0);
    }

    public static LimaRecipeInput createWithSize(IItemHandler container, int size)
    {
        return new LimaRecipeInput(container, size, 0);
    }

    public static LimaRecipeInput createWithOffset(IItemHandler container, int ingredientOffset)
    {
        return new LimaRecipeInput(container, container.getSlots(), ingredientOffset);
    }

    private final IItemHandler container;
    private final int size;
    private final int ingredientOffset;

    public LimaRecipeInput(IItemHandler container, int size, int ingredientOffset)
    {
        if (size < 1 || size > container.getSlots()) throw new IllegalArgumentException("LimaItemInput exceeds valid parent container size (0," + container.getSlots() + "]");
        this.container = container;
        this.size = size;
        this.ingredientOffset = ingredientOffset;
    }

    public IItemHandler getContainer()
    {
        return container;
    }

    public int getIngredientIndex(int index)
    {
        return index + ingredientOffset;
    }

    public ItemStack extractFromContainer(int index, int count, boolean simulate)
    {
        return container.extractItem(getIngredientIndex(index), count, simulate);
    }

    @Override
    public ItemStack getItem(int index)
    {
        return container.getStackInSlot(getIngredientIndex(index));
    }

    @Override
    public int size()
    {
        return size;
    }
}