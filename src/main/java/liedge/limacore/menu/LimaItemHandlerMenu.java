package liedge.limacore.menu;

import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.limacore.menu.slot.LimaHandlerSlot;
import liedge.limacore.menu.slot.RecipeOutputSlot;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public abstract class LimaItemHandlerMenu<CTX extends ItemHolderBlockEntity> extends LimaMenu<CTX>
{
    protected LimaItemHandlerMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId, inventory, menuContext);
    }

    protected void addHandlerSlot(IItemHandlerModifiable container, int slot, int x, int y, boolean allowInsert)
    {
        addSlot(new LimaHandlerSlot(container, slot, x, y, allowInsert));
    }

    protected void addHandlerSlot(IItemHandlerModifiable container, int slot, int x, int y)
    {
        addSlot(new LimaHandlerSlot(container, slot, x, y));
    }

    protected void addHandlerSlotsGrid(IItemHandlerModifiable container, int startIndex, int x, int y, int columns, int rows)
    {
        addSlotsGrid(container, startIndex, x, y, columns, rows, LimaHandlerSlot::new);
    }

    protected void addHandlerRecipeOutputSlot(IItemHandlerModifiable container, int slot, int x, int y, RecipeType<?> recipeType)
    {
        addSlot(new RecipeOutputSlot(container, slot, x, y, playerInventory.player, recipeType));
    }

    protected void addHandlerRecipeOutputSlot(IItemHandlerModifiable container, int slot, int x, int y, Holder<RecipeType<?>> recipeTypeHolder)
    {
        addHandlerRecipeOutputSlot(container, slot, x, y, recipeTypeHolder.value());
    }

    protected void addHandlerRecipeOutputSlotGrid(IItemHandlerModifiable container, int startIndex, int x, int y, int columns, int rows, RecipeType<?> recipeType)
    {
        addSlotsGrid(container, startIndex, x, y, columns, rows, (ctr, slot, sx, sy) -> new RecipeOutputSlot(ctr, slot, sx, sy, playerInventory.player, recipeType));
    }

    protected void addHandlerRecipeOutputSlotGrid(IItemHandlerModifiable container, int startIndex, int x, int y, int columns, int rows, Holder<RecipeType<?>> recipeTypeHolder)
    {
        addHandlerRecipeOutputSlotGrid(container, startIndex, x, y, columns, rows, recipeTypeHolder.value());
    }
}