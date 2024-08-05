package liedge.limacore.item;

import liedge.limacore.lib.ModResources;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public abstract class LimaCreativeTabDisplayGenerator implements CreativeModeTab.DisplayItemsGenerator
{
    public static LimaCreativeTabDisplayGenerator primaryTab(ResourceLocation tabId, Collection<DeferredHolder<Block, ? extends Block>> deferredBlocks, Collection<DeferredHolder<Item, ? extends Item>> deferredItems)
    {
        return new ModDefaultTab(tabId, deferredBlocks, deferredItems);
    }

    public static LimaCreativeTabDisplayGenerator customContents(ResourceLocation tabId, List<ItemLike> items)
    {
        return new CustomContents(tabId, items);
    }

    public static LimaCreativeTabDisplayGenerator customContents(ResourceLocation tabId, ItemLike... items)
    {
        return customContents(tabId, List.of(items));
    }

    private final ResourceLocation tabId;

    private CreativeModeTab.TabVisibility tabVisibility = CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;

    protected LimaCreativeTabDisplayGenerator(ResourceLocation tabId)
    {
        this.tabId = tabId;
    }

    protected abstract Stream<ItemLike> tabItems();

    public LimaCreativeTabDisplayGenerator setTabVisibility(CreativeModeTab.TabVisibility tabVisibility)
    {
        this.tabVisibility = tabVisibility;
        return this;
    }

    public CreativeModeTab.Builder startBuilder()
    {
        return CreativeModeTab.builder()
                .title(Component.translatable(ModResources.prefixIdTranslationKey("creative_tab", tabId)))
                .displayItems(this);
    }

    public void modifyExistingTab(final BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey().location().equals(tabId))
        {
            accept(event.getParameters(), event);
        }
    }

    @Override
    public void accept(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output)
    {
        tabItems()
                .filter(item -> !item.equals(Items.AIR))
                .forEach(item -> {
                    if (item instanceof LimaCreativeTabFillerItem fillerItem)
                    {
                        fillerItem.addStacksToCreativeTab(tabId, parameters, output, tabVisibility);
                    }
                    else
                    {
                        output.accept(item, tabVisibility);
                    }
                });
    }

    private static class CustomContents extends LimaCreativeTabDisplayGenerator
    {
        private final List<ItemLike> items;

        private CustomContents(ResourceLocation tabId, List<ItemLike> items)
        {
            super(tabId);
            this.items = items;
        }

        @Override
        protected Stream<ItemLike> tabItems()
        {
            return items.stream();
        }
    }

    private static class ModDefaultTab extends LimaCreativeTabDisplayGenerator
    {
        private final Collection<DeferredHolder<Block, ? extends Block>> deferredBlocks;
        private final Collection<DeferredHolder<Item, ? extends Item>> deferredItems;

        private ModDefaultTab(ResourceLocation tabId, Collection<DeferredHolder<Block, ? extends Block>> deferredBlocks, Collection<DeferredHolder<Item, ? extends Item>> deferredItems)
        {
            super(tabId);
            this.deferredBlocks = deferredBlocks;
            this.deferredItems = deferredItems;
        }

        @Override
        protected Stream<ItemLike> tabItems()
        {
            Stream.Builder<ItemLike> builder = Stream.builder();
            deferredBlocks.forEach(o -> builder.add(o.get()));
            deferredItems.forEach(o -> builder.add(o.get()));
            return builder.build();
        }
    }
}