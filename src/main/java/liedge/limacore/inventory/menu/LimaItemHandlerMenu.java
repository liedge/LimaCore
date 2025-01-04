package liedge.limacore.inventory.menu;

import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.limacore.capability.itemhandler.LimaItemHandlerBase;
import net.minecraft.world.entity.player.Inventory;

public abstract class LimaItemHandlerMenu<CTX extends ItemHolderBlockEntity> extends LimaMenu<CTX>
{
    protected LimaItemHandlerMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId, inventory, menuContext);
    }

    @Override
    protected LimaItemHandlerBase menuContainer()
    {
        return menuContext.getItemHandler();
    }
}