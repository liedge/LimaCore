package liedge.limacore.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.slot.LimaItemSlot;
import liedge.limacore.transfer.item.ItemHolderBlockEntity;
import liedge.limacore.transfer.item.LimaBlockEntityItems;
import net.minecraft.world.entity.player.Inventory;

import java.util.Objects;

public abstract class BlockEntityMenu<CTX extends ItemHolderBlockEntity> extends LimaMenu<CTX>
{
    protected BlockEntityMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId, inventory, menuContext);
    }

    // Slot addition helpers
    protected void addSlot(BlockContentsType contentsType, int handlerIndex, int x, int y)
    {
        addSlot(new LimaItemSlot(getInventory(contentsType), handlerIndex, x, y));
    }

    protected void addSlotsGrid(BlockContentsType contentsType, int indexStart, int x, int y, int columns, int rows)
    {
        addSlotsGrid(getInventory(contentsType), indexStart, x, y, columns, rows, LimaItemSlot::new);
    }

    protected void addOutputSlot(int handlerIndex, int x, int y)
    {
        addSlot(new LimaItemSlot(getInventory(BlockContentsType.OUTPUT), handlerIndex, x, y, false));
    }

    protected void addOutputSlotsGrid(int indexStart, int x, int y, int columns, int rows)
    {
        addSlotsGrid(getInventory(BlockContentsType.OUTPUT), indexStart, x, y, columns, rows, (ctr, slot, sx, sy) ->
                new LimaItemSlot(ctr, slot, sx, sy, false));
    }

    private LimaBlockEntityItems getInventory(BlockContentsType contentsType)
    {
        return Objects.requireNonNull(menuContext.getItems(contentsType));
    }
}