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
import java.util.function.UnaryOperator;

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

    public <B extends Block, I extends BlockItem> DeferredItem<I> registerCustomBlockItem(String name, Supplier<? extends B> block, BiFunction<? super B, Item.Properties, ? extends I> constructor, Supplier<Item.Properties> properties)
    {
        return registerItem(name, props -> constructor.apply(block.get(), props), () -> properties.get().useBlockDescriptionPrefix());
    }

    public <B extends Block, I extends BlockItem> DeferredItem<I> registerCustomBlockItem(String name, Supplier<? extends B> block, BiFunction<? super B, Item.Properties, ? extends I> constructor, UnaryOperator<Item.Properties> properties)
    {
        return registerCustomBlockItem(name, block, constructor, () -> properties.apply(new Item.Properties()));
    }

    public <B extends Block, I extends BlockItem> DeferredItem<I> registerCustomBlockItem(String name, Supplier<? extends B> block, BiFunction<? super B, Item.Properties, ? extends I> constructor)
    {
        return registerCustomBlockItem(name, block, constructor, UnaryOperator.identity());
    }

    public <I extends BlockItem> DeferredItem<I> registerCustomBlockItem(Holder<Block> holder, BiFunction<Block, Item.Properties, ? extends I> constructor, Supplier<Item.Properties> properties)
    {
        return registerCustomBlockItem(LimaRegistryUtil.getBlockName(holder), holder::value, constructor, properties);
    }

    public <I extends BlockItem> DeferredItem<I> registerCustomBlockItem(Holder<Block> holder, BiFunction<Block, Item.Properties, ? extends I> constructor, UnaryOperator<Item.Properties> properties)
    {
        return registerCustomBlockItem(LimaRegistryUtil.getBlockName(holder), holder::value, constructor, properties);
    }

    public <I extends BlockItem> DeferredItem<I> registerCustomBlockItem(Holder<Block> holder, BiFunction<Block, Item.Properties, ? extends I> constructor)
    {
        return registerCustomBlockItem(holder, constructor, UnaryOperator.identity());
    }
}