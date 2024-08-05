package liedge.limacore.inventory.menu;

import liedge.limacore.capability.itemhandler.ItemHandlerHolder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeType;

public abstract class LimaItemHandlerMenu<CTX extends ItemHandlerHolder> extends LimaMenu<CTX>
{
    protected LimaItemHandlerMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId, inventory, menuContext);
    }

    // Convenience slot factories for ItemHandlerHolder menu contexts
    protected void addContextSlot(int slotIndex, int xPos, int yPos)
    {
        addSlot(new LimaItemHandlerMenuSlot(menuContext().getItemHandler(), slotIndex, xPos, yPos));
    }

    protected void addContextSlot(int slotIndex, int xPos, int yPos, boolean allowInsert)
    {
        addSlot(new LimaItemHandlerMenuSlot(menuContext().getItemHandler(), slotIndex, xPos, yPos, allowInsert));
    }

    protected void addContextSlotsGrid(int startIndex, int xPos, int yPos, int columns, int rows)
    {
        addSlotsGrid(menuContext().getItemHandler(), startIndex, xPos, yPos, columns, rows, LimaItemHandlerMenuSlot::new);
    }

    protected void addContextRecipeResultSlot(int slotIndex, int xPos, int yPos, RecipeType<?> recipeType)
    {
        addRecipeResultSlot(menuContext().getItemHandler(), slotIndex, xPos, yPos, recipeType);
    }

    protected void addContextRecipeResultSlot(int slotIndex, int xPos, int yPos, Holder<RecipeType<?>> holder)
    {
        addContextRecipeResultSlot(slotIndex, xPos, yPos, holder.value());
    }
}