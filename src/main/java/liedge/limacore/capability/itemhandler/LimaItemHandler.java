package liedge.limacore.capability.itemhandler;

import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public interface LimaItemHandler extends IItemHandlerModifiable
{
    ItemContainerContents copyToComponent();

    void copyFromComponent(ItemContainerContents contents);

    void onContentsChanged(int slot);
}