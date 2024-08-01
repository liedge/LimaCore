package liedge.limacore.registry;

import com.google.common.base.Preconditions;
import liedge.limacore.util.LimaCoreUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class LimaBlockDeferredRegister
{
    private final Map<LimaDeferredBlock<?, ?>, Pair<Supplier<? extends Block>, Supplier<?>>> entries = new LinkedHashMap<>();
    private final Set<LimaDeferredBlock<?, ?>> entriesView = Collections.unmodifiableSet(entries.keySet());

    private final String modid;

    private boolean seenRegisterEvent;
    private boolean registeredToBus;

    public LimaBlockDeferredRegister(String modid)
    {
        this.modid = modid;
    }

    public void registerToBus(IEventBus bus)
    {
        Preconditions.checkState(!registeredToBus, "Deferred register has already been registered to an event bus");
        this.registeredToBus = true;
        bus.addListener(this::registerEntries);
    }

    public Collection<LimaDeferredBlock<?, ?>> getRegistryEntries()
    {
        return entriesView;
    }

    private <B extends Block, I> LimaDeferredBlock<B, I> registerInternal(final String name, final Function<ResourceLocation, ? extends B> blockFunction, final @Nullable BiFunction<ResourceLocation, ? super B, ? extends I> itemBiFunction)
    {
        if (seenRegisterEvent)
        {
            throw new IllegalStateException("Cannot add new entries to block deferred register after RegisterEvent has been fired.");
        }

        Objects.requireNonNull(name);
        Objects.requireNonNull(blockFunction);

        final ResourceLocation key = ResourceLocation.fromNamespaceAndPath(modid, name);
        final LimaDeferredBlock<B, I> holder = new LimaDeferredBlock<>(key);
        final Supplier<? extends I> itemSupplier = itemBiFunction != null ? () -> itemBiFunction.apply(key, holder.get()) : null;

        if (entries.putIfAbsent(holder, new Pair<>(() -> blockFunction.apply(key), itemSupplier)) != null)
        {
            throw new IllegalArgumentException("Duplicate registration " + name);
        }

        return holder;
    }

    //#region Helper registration methods
    public <B extends Block> LimaDeferredBlock<B, Void> registerBlockOnly(String name, Supplier<? extends B> supplier)
    {
        return registerInternal(name, key -> supplier.get(), null);
    }

    public <B extends Block> LimaDeferredBlock<B, Void> registerBlockOnly(String name, Function<ResourceLocation, ? extends B> function)
    {
        return registerInternal(name, function, null);
    }

    public <B extends Block, I extends BlockItem> LimaDeferredBlock<B, I> registerBlockAndItem(String name, Function<ResourceLocation, ? extends B> blockFunction, BiFunction<ResourceLocation, ? super B, ? extends I> itemFunction)
    {
        return registerInternal(name, blockFunction, itemFunction);
    }

    public <B extends Block, I extends BlockItem> LimaDeferredBlock<B, I> registerBlockAndItem(String name, Supplier<? extends B> supplier, Function<? super B, ? extends I> itemFunction)
    {
        return registerBlockAndItem(name, key -> supplier.get(), (id, b) -> itemFunction.apply(b));
    }

    public <B extends Block> LimaDeferredBlock<B, BlockItem> registerBlockAndSimpleItem(String name, Function<ResourceLocation, ? extends B> blockFunction)
    {
        return registerBlockAndItem(name, blockFunction, (id, b) -> new BlockItem(b, new Item.Properties()));
    }

    public <B extends Block> LimaDeferredBlock<B, BlockItem> registerBlockAndSimpleItem(String name, Supplier<? extends B> supplier)
    {
        return registerBlockAndSimpleItem(name, key -> supplier.get());
    }

    public LimaDeferredBlock<Block, BlockItem> registerSimpleBlockWithItem(String name, BlockBehaviour.Properties properties)
    {
        return registerBlockAndSimpleItem(name, () -> new Block(properties));
    }
    //#endregion

    private void registerEntries(final RegisterEvent event)
    {
        if (event.getRegistryKey() == Registries.BLOCK)
        {
            this.seenRegisterEvent = true;
            entries.forEach((holder, supplierPair) -> {
                event.register(Registries.BLOCK, holder.getId(), () -> supplierPair.getA().get());
                holder.bindBlock();
            });

        }
        else if (event.getRegistryKey() == Registries.ITEM)
        {
            this.seenRegisterEvent = true;
            entries.forEach((holder, supplierPair) -> {
                Supplier<?> sup = supplierPair.getB();
                if (sup != null)
                {
                    event.register(Registries.ITEM, holder.getId(), () -> LimaCoreUtil.castOrThrow(BlockItem.class, sup.get()));
                    holder.bindItem();
                }
            });
        }
    }
}