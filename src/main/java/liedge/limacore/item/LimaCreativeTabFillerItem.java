package liedge.limacore.item;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.Collection;
import java.util.List;

public interface LimaCreativeTabFillerItem extends ItemLike
{
    // Helpers
    static void addItemToTab(ResourceLocation tabId, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output, CreativeModeTab.TabVisibility tabVisibility, Item item)
    {
        if (item instanceof LimaCreativeTabFillerItem fillerItem)
        {
            if (fillerItem.addDefaultInstanceToCreativeTab(tabId)) output.accept(item, tabVisibility);
            fillerItem.addAdditionalToCreativeTab(tabId, parameters, output, tabVisibility);
        }
        else
        {
            output.accept(item, tabVisibility);
        }
    }

    static void addToTab(ResourceLocation tabId, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output, CreativeModeTab.TabVisibility tabVisibility, ItemLike itemLike)
    {
        addItemToTab(tabId, parameters, output, tabVisibility, itemLike.asItem());
    }

    static void addToTab(ResourceLocation tabId, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output, CreativeModeTab.TabVisibility tabVisibility, Collection<ItemLike> items)
    {
        items.forEach(item -> addToTab(tabId, parameters, output, tabVisibility, item));
    }

    static void addToTab(ResourceLocation tabId, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output, CreativeModeTab.TabVisibility tabVisibility, ItemLike... items)
    {
        addToTab(tabId, parameters, output, tabVisibility, List.of(items));
    }

    static void addHoldersToTab(ResourceLocation tabId, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output, CreativeModeTab.TabVisibility tabVisibility, Collection<? extends Holder<? extends ItemLike>> holders)
    {
        holders.forEach(holder -> addToTab(tabId, parameters, output, tabVisibility, holder.value()));
    }

    // Instance methods
    default boolean addDefaultInstanceToCreativeTab(ResourceLocation tabId)
    {
        return true;
    }

    default void addAdditionalToCreativeTab(ResourceLocation tabId, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output, CreativeModeTab.TabVisibility tabVisibility)
    {
        output.accept(this, tabVisibility);
    }
}