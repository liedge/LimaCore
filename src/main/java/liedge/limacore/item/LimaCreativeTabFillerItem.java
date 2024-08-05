package liedge.limacore.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

public interface LimaCreativeTabFillerItem extends ItemLike
{
    default void addStacksToCreativeTab(ResourceLocation tabId, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output, CreativeModeTab.TabVisibility defaultTabVisibility)
    {
        output.accept(this, defaultTabVisibility);
    }
}