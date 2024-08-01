package liedge.limacore.item;

import liedge.limacore.lib.ModResources;
import liedge.limacore.registry.LimaDeferredBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Collection;

/**
 * Fills a creative tab with deferred blocks and items based on the order that they are registered. Blocks will be filled first followed by items.
 */
public class LimaCreativeTabGenerator implements CreativeModeTab.DisplayItemsGenerator
{
    /**
     * Starts a CreativeModeTab.Builder with the title and display items generator automatically provided.
     * @param tabId ID of the creative tab used for creating the title component
     * @param blocks Collection of deferred blocks to be placed in the tab
     * @param items Collection of deferred items to be placed in the tab
     */
    public static CreativeModeTab.Builder tabBuilder(ResourceLocation tabId, Collection<LimaDeferredBlock<?, ?>> blocks, Collection<DeferredHolder<Item, ? extends Item>> items)
    {
        return CreativeModeTab.builder()
                .title(Component.translatable(ModResources.prefixIdTranslationKey("creative_tab", tabId)))
                .displayItems(new LimaCreativeTabGenerator(tabId, blocks, items));
    }

    private final ResourceLocation tabId;
    private final Collection<LimaDeferredBlock<?, ?>> blocks;
    private final Collection<DeferredHolder<Item, ? extends Item>> items;

    protected LimaCreativeTabGenerator(ResourceLocation tabId, Collection<LimaDeferredBlock<?, ?>> blocks, Collection<DeferredHolder<Item, ? extends Item>> items)
    {
        this.tabId = tabId;
        this.blocks = blocks;
        this.items = items;
    }

    @Override
    public void accept(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output)
    {
        blocks.stream().filter(i -> i.asItem() != Items.AIR).forEach(i -> addToTab(i, params, output));
        items.stream().map(DeferredHolder::get).forEach(i -> addToTab(i, params, output));
    }

    private void addToTab(ItemLike item, CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output)
    {
        if (item instanceof LimaCreativeTabStacksProvider provider)
        {
            if (provider.addDefaultItemStack()) output.accept(item);
            provider.generateCreativeTabStacks(tabId, params, output);
        }
        else
        {
            output.accept(item);
        }
    }
}