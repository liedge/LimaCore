package liedge.limacore.capability.itemhandler;

import liedge.limacore.capability.IOAccess;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public interface LimaItemHandlerBase extends IItemHandlerModifiable
{
    IOAccess getSlotIOAccess(int slot);

    ItemContainerContents copyToComponent();

    void copyFromComponent(ItemContainerContents contents);
}