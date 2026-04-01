package liedge.limacore.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.slot.LimaItemSlot;
import liedge.limacore.menu.slot.RecipeResultSlot;
import liedge.limacore.transfer.item.ItemHolderBlockEntity;
import liedge.limacore.transfer.item.LimaBlockEntityItems;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.UnaryOperator;

public abstract class BlockEntityMenu<CTX extends ItemHolderBlockEntity> extends LimaMenu<CTX>
{
    protected BlockEntityMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId, inventory, menuContext);
    }

    // Slot addition helpers
    protected void addSlot(BlockContentsType contentsType, int handlerIndex, int x, int y, UnaryOperator<LimaItemSlot> modifier)
    {
        addSlot(modifier.apply(LimaItemSlot.create(getInventory(contentsType), handlerIndex, x, y)));
    }

    protected void addSlot(BlockContentsType contentsType, int handlerIndex, int x, int y)
    {
        addSlot(LimaItemSlot.create(getInventory(contentsType), handlerIndex, x, y));
    }

    protected void addSlotsGrid(BlockContentsType contentsType, int indexStart, int x, int y, int columns, int rows, UnaryOperator<LimaItemSlot> modifier)
    {
        addSlotsGrid(getInventory(contentsType), indexStart, x, y, columns, rows, (container, index, sx, sy) ->
                modifier.apply(LimaItemSlot.create(container, index, sx, sy)));
    }

    protected void addSlotsGrid(BlockContentsType contentsType, int indexStart, int x, int y, int columns, int rows)
    {
        addSlotsGrid(getInventory(contentsType), indexStart, x, y, columns, rows, LimaItemSlot::new);
    }

    protected void addOutputSlot(int handlerIndex, int x, int y)
    {
        addSlot(BlockContentsType.OUTPUT, handlerIndex, x, y, slot -> slot.allowPlacement(false));
    }

    protected void addOutputSlotsGrid(int indexStart, int x, int y, int columns, int rows)
    {
        addSlotsGrid(BlockContentsType.OUTPUT, indexStart, x, y, columns, rows, slot -> slot.allowPlacement(false));
    }

    protected void addRecipeOutputSlot(int handlerIndex, int x, int y, RecipeType<?> recipeType)
    {
        addSlot(new RecipeResultSlot(getInventory(BlockContentsType.OUTPUT), handlerIndex, x, y, playerInventory.player, recipeType));
    }

    protected void addRecipeOutputSlot(int handlerIndex, int x, int y, Holder<RecipeType<?>> typeHolder)
    {
        addSlot(new RecipeResultSlot(getInventory(BlockContentsType.OUTPUT), handlerIndex, x, y, playerInventory.player, typeHolder.value()));
    }

    protected void addRecipeOutputSlotGrid(int indexStart, int x, int y, int columns, int rows, RecipeType<?> recipeType)
    {
        addSlotsGrid(getInventory(BlockContentsType.OUTPUT), indexStart, x, y, columns, rows, (container, index, sx, sy) ->
                new RecipeResultSlot(container, index, sx, sy, playerInventory.player, recipeType));
    }

    protected void addRecipeOutputSlotGrid(int indexStart, int x, int y, int columns, int rows, Holder<RecipeType<?>> typeHolder)
    {
        addSlotsGrid(getInventory(BlockContentsType.OUTPUT), indexStart, x, y, columns, rows, (container, index, sx, sy) ->
                new RecipeResultSlot(container, index, sx, sy, playerInventory.player, typeHolder.value()));
    }

    protected LimaBlockEntityItems getInventory(BlockContentsType contentsType)
    {
        return menuContext.getItemsOrThrow(contentsType);
    }
}