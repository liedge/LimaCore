package liedge.limacore.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.limacore.capability.itemhandler.LimaItemHandler;
import liedge.limacore.menu.slot.LimaHandlerSlot;
import liedge.limacore.menu.slot.RecipeOutputSlot;
import liedge.limacore.util.LimaItemUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Predicate;

public abstract class BlockEntityMenu<CTX extends ItemHolderBlockEntity> extends LimaMenu<CTX>
{
    protected BlockEntityMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId, inventory, menuContext);
    }

    // Slot addition helpers
    protected void addSlot(BlockContentsType contentsType, int handlerIndex, int x, int y)
    {
        addSlot(new LimaHandlerSlot(getInventory(contentsType), handlerIndex, x, y));
    }

    protected void addSlot(BlockContentsType contentsType, int handlerIndex, int x, int y, Predicate<ItemStack> quickTransferPredicate)
    {
        addSlot(new LimaHandlerSlot(getInventory(contentsType), handlerIndex, x, y, true, quickTransferPredicate));
    }

    protected void addSlotsGrid(BlockContentsType contentsType, int indexStart, int x, int y, int columns, int rows)
    {
        addSlotsGrid(getInventory(contentsType), indexStart, x, y, columns, rows, LimaHandlerSlot::new);
    }

    protected void addSlotsGrid(BlockContentsType contentsType, int indexStart, int x, int y, int columns, int rows, Predicate<ItemStack> quickTransferPredicate)
    {
        addSlotsGrid(getInventory(contentsType), indexStart, x, y, columns, rows, (ctr, slot, sx, sy) ->
                new LimaHandlerSlot(ctr, slot, sx, sy, true, quickTransferPredicate));
    }

    protected void addOutputSlot(int handlerIndex, int x, int y)
    {
        addSlot(new LimaHandlerSlot(getInventory(BlockContentsType.OUTPUT), handlerIndex, x, y, false, LimaItemUtil.ALWAYS_FALSE));
    }

    protected void addOutputSlotsGrid(int indexStart, int x, int y, int columns, int rows)
    {
        addSlotsGrid(getInventory(BlockContentsType.OUTPUT), indexStart, x, y, columns, rows, (ctr, slot, sx, sy) ->
                new LimaHandlerSlot(ctr, slot, sx, sy, false, LimaItemUtil.ALWAYS_FALSE));
    }

    protected void addRecipeOutputSlot(int handlerIndex, int x, int y, RecipeType<?> recipeType)
    {
        addSlot(new RecipeOutputSlot(getInventory(BlockContentsType.OUTPUT), handlerIndex, x, y, playerInventory.player, recipeType));
    }

    protected void addRecipeOutputSlot(int handlerIndex, int x, int y, Holder<RecipeType<?>> holder)
    {
        addRecipeOutputSlot(handlerIndex, x, y, holder.value());
    }

    protected void addRecipeOutputSlotsGrid(int indexStart, int x, int y, int columns, int rows, RecipeType<?> recipeType)
    {
        addSlotsGrid(getInventory(BlockContentsType.OUTPUT), indexStart, x, y, columns, rows, (ctr, slot, sx, sy) ->
                new RecipeOutputSlot(ctr, slot, sx, sy, playerInventory.player, recipeType));
    }

    protected void addRecipeOutputSlotsGrid(int indexStart, int x, int y, int columns, int rows, Holder<RecipeType<?>> holder)
    {
        addRecipeOutputSlotsGrid(indexStart, x, y, columns, rows, holder.value());
    }

    private LimaItemHandler getInventory(BlockContentsType contentsType)
    {
        return menuContext.getItemHandlerOrThrow(contentsType);
    }
}