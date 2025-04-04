package liedge.limacore.registry;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import oshi.util.tuples.Pair;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class LimaDeferredBlocksWithItems extends DeferredRegister.Blocks
{
    public static LimaDeferredBlocksWithItems create(String namespace)
    {
        return new LimaDeferredBlocksWithItems(namespace);
    }

    private final Map<DeferredBlockWithItem<?, ?>, Pair<Supplier<? extends Block>, Supplier<? extends BlockItem>>> pairEntries = new LinkedHashMap<>();
    private final Map<ResourceLocation, ResourceLocation> itemAliases = new Object2ObjectOpenHashMap<>();

    private boolean seenRegisterEvent;

    private LimaDeferredBlocksWithItems(String namespace)
    {
        super(namespace);
    }

    @Override
    public void register(IEventBus bus)
    {
        super.register(bus);
        bus.addListener(this::registerPairEntries);
    }

    @Override
    public Collection<DeferredHolder<Block, ? extends Block>> getEntries()
    {
        ObjectList<DeferredHolder<Block, ? extends Block>> entries = new ObjectArrayList<>();
        entries.addAll(super.getEntries());
        entries.addAll(pairEntries.keySet());
        return ObjectLists.unmodifiable(entries);
    }

    public void addBlockAndItemAlias(ResourceLocation from, ResourceLocation to)
    {
        addAlias(from, to);
        itemAliases.put(from, to);
    }

    public Collection<DeferredHolder<Block, ? extends Block>> getEntriesWithItemsOnly()
    {
        return List.copyOf(pairEntries.keySet());
    }

    public <B extends Block, I extends BlockItem> DeferredBlockWithItem<B, I> registerBlockAndItem(String name, Function<ResourceLocation, ? extends B> blockFunction, BiFunction<ResourceLocation, ? super B, ? extends I> itemFunction)
    {
        return registerInternal(name, blockFunction, itemFunction);
    }

    public <B extends Block, I extends BlockItem> DeferredBlockWithItem<B, I> registerBlockAndItem(String name, Supplier<? extends B> blockSupplier, Function<? super B, ? extends I> itemFunction)
    {
        return registerBlockAndItem(name, key -> blockSupplier.get(), (key, block) -> itemFunction.apply(block));
    }

    public <B extends Block> DeferredBlockWithItem<B, BlockItem> registerBlockAndSimpleItem(String name, Function<ResourceLocation, ? extends B> blockFunction, Item.Properties itemProperties)
    {
        return registerBlockAndItem(name, blockFunction, (key, block) -> new BlockItem(block, itemProperties));
    }

    public <B extends Block> DeferredBlockWithItem<B, BlockItem> registerBlockAndSimpleItem(String name, Function<ResourceLocation, ? extends B> blockFunction)
    {
        return registerBlockAndSimpleItem(name, blockFunction, new Item.Properties());
    }

    public <B extends Block> DeferredBlockWithItem<B, BlockItem> registerBlockAndSimpleItem(String name, Supplier<? extends B> blockSupplier, Item.Properties itemProperties)
    {
        return registerBlockAndSimpleItem(name, key -> blockSupplier.get(), itemProperties);
    }

    public <B extends Block> DeferredBlockWithItem<B, BlockItem> registerBlockAndSimpleItem(String name, Supplier<? extends B> blockSupplier)
    {
        return registerBlockAndSimpleItem(name, blockSupplier, new Item.Properties());
    }

    public DeferredBlockWithItem<Block, BlockItem> registerSimpleBlockAndItem(String name, BlockBehaviour.Properties blockProperties, Item.Properties itemProperties)
    {
        return registerBlockAndSimpleItem(name, () -> new Block(blockProperties), itemProperties);
    }

    public DeferredBlockWithItem<Block, BlockItem> registerSimpleBlockAndItem(String name, BlockBehaviour.Properties blockProperties)
    {
        return registerSimpleBlockAndItem(name, blockProperties, new Item.Properties());
    }

    private <B extends Block, I extends BlockItem> DeferredBlockWithItem<B, I> registerInternal(final String name, final Function<ResourceLocation, ? extends B> blockFunction, final BiFunction<ResourceLocation, ? super B, ? extends I> itemFunction)
    {
        if (seenRegisterEvent) throw new IllegalStateException("Cannot add new entries to block deferred register after RegisterEvent has been fired.");

        Objects.requireNonNull(blockFunction);
        Objects.requireNonNull(itemFunction);
        final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(getNamespace(), Objects.requireNonNull(name));

        DeferredBlockWithItem<B, I> holder = DeferredBlockWithItem.createBlockAndItemPair(id);
        Supplier<? extends B> blockSupplier = () -> blockFunction.apply(id);
        Supplier<? extends I> itemSupplier = () -> itemFunction.apply(id, holder.get());
        Pair<Supplier<? extends Block>, Supplier<? extends BlockItem>> pair = new Pair<>(blockSupplier, itemSupplier);

        if (pairEntries.putIfAbsent(holder, pair) != null)
        {
            throw new IllegalArgumentException("Duplicate registration " + name);
        }

        return holder;
    }

    private void registerPairEntries(final RegisterEvent event)
    {
        if (event.getRegistryKey() == Registries.BLOCK)
        {
            this.seenRegisterEvent = true;
            pairEntries.forEach((holder, pair) -> {
                event.register(Registries.BLOCK, holder.getId(), () -> pair.getA().get());
                holder.bindBlock();
            });
        }
        else if (event.getRegistryKey() == Registries.ITEM)
        {
            this.seenRegisterEvent = true;
            Registry<Item> registry = Objects.requireNonNull(event.getRegistry(Registries.ITEM), "Item registry missing. This should not happen.");
            itemAliases.forEach(registry::addAlias);
            pairEntries.forEach((holder, pair) -> {
                event.register(Registries.ITEM, holder.getId(), () -> pair.getB().get());
                holder.bindItem();
            });
        }
    }
}