package liedge.limacore.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;

import javax.annotation.Nullable;
import java.util.Objects;

public class DeferredBlockPair<B extends Block, I extends BlockItem> extends DeferredBlock<B>
{
    public static <B extends Block, I extends BlockItem> DeferredBlockPair<B, I> createBlockAndItemPair(ResourceLocation id)
    {
        return new DeferredBlockPair<>(ResourceKey.create(Registries.BLOCK, id), ResourceKey.create(Registries.ITEM, id));
    }

    public static <B extends Block, I extends BlockItem> DeferredBlockPair<B, I> createBlockAndItemPair(ResourceKey<Block> blockKey, ResourceKey<Item> itemKey)
    {
        return new DeferredBlockPair<>(blockKey, itemKey);
    }

    private final ResourceKey<Item> itemKey;
    private @Nullable Holder<Item> itemHolder;

    private DeferredBlockPair(ResourceKey<Block> blockKey, ResourceKey<Item> itemKey)
    {
        super(blockKey);
        this.itemKey = itemKey;
    }

    public Holder<Item> getItemHolder()
    {
        return Objects.requireNonNull(itemHolder, "Block '" + getId() + "' does not have a block item registered.");
    }

    @SuppressWarnings("unchecked")
    public I asItem()
    {
        return (I) getItemHolder().value();
    }

    void bindBlock()
    {
        bind(false);
    }

    void bindItem()
    {
        if (itemHolder == null)
        {
            itemHolder = BuiltInRegistries.ITEM.getHolder(itemKey).orElse(null);
        }
    }
}