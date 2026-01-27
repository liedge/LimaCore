package liedge.limacore.registry;

import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class LimaDeferredItems extends DeferredRegister.Items
{
    public static LimaDeferredItems create(String namespace)
    {
        return new LimaDeferredItems(namespace);
    }

    private LimaDeferredItems(String namespace)
    {
        super(namespace);
    }

    public <B extends Block, I extends BlockItem> DeferredItem<I> registerBlockItem(String name, Supplier<? extends B> blockSupplier, BiFunction<? super B, Item.Properties, ? extends I> constructor, Item.Properties properties)
    {
        return registerItem(name, p -> constructor.apply(blockSupplier.get(), p), properties);
    }

    public <B extends Block, I extends BlockItem> DeferredItem<I> registerBlockItem(String name, Supplier<? extends B> blockSupplier, BiFunction<? super B, Item.Properties, ? extends I> constructor)
    {
        return registerBlockItem(name, blockSupplier, constructor, new Item.Properties());
    }

    public <I extends BlockItem> DeferredItem<I> registerBlockItem(Holder<Block> holder, BiFunction<Block, Item.Properties, ? extends I> constructor, Item.Properties properties)
    {
        return registerBlockItem(LimaRegistryUtil.getBlockName(holder), holder::value, constructor, properties);
    }

    public <I extends BlockItem> DeferredItem<I> registerBlockItem(Holder<Block> holder, BiFunction<Block, Item.Properties, ? extends I> constructor)
    {
        return registerBlockItem(holder, constructor, new Item.Properties());
    }
}