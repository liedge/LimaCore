package liedge.limacore.data.generation.loot;

import liedge.limacore.util.LimaRegistryUtil;
import liedge.limacore.world.loot.SaveBlockEntityFunction;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class LimaBlockLootSubProvider extends BlockLootSubProvider implements LimaLootSubProviderExtensions
{
    private final String modid;

    protected LimaBlockLootSubProvider(HolderLookup.Provider provider, String modid)
    {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        this.modid = modid;
    }

    @Override
    protected Iterable<Block> getKnownBlocks()
    {
        return LimaRegistryUtil.allNamespaceRegistryValues(modid, BuiltInRegistries.BLOCK).toList();
    }

    protected void add(Supplier<? extends Block> supplier, LootTable.Builder builder)
    {
        add(supplier.get(), builder);
    }

    protected void dropSelf(Supplier<? extends Block> supplier)
    {
        dropSelf(supplier.get());
    }

    protected void dropSelf(Collection<? extends Supplier<Block>> suppliers)
    {
        suppliers.forEach(this::dropSelf);
    }

    @SafeVarargs
    protected final void dropSelf(Supplier<? extends Block>... suppliers)
    {
        Stream.of(suppliers).forEach(this::dropSelf);
    }

    protected void dropSelfWithEntity(Block block)
    {
        add(block, singlePoolTable(applyExplosionCondition(block, singleItemPool(block)).apply(SaveBlockEntityFunction.saveBlockEntityData())));
    }

    protected void dropSelfWithEntity(Supplier<? extends Block> supplier)
    {
        dropSelfWithEntity(supplier.get());
    }

    protected void oreDrop(Supplier<? extends Block> oreBlock, Supplier<? extends Item> rawOreItem)
    {
        add(oreBlock.get(), createOreDrop(oreBlock.get(), rawOreItem.get()));
    }

    protected <T extends Comparable<T> & StringRepresentable> LootItemCondition.Builder matchStateProperty(Supplier<? extends Block> supplier, Property<T> property, T value)
    {
        return LootItemBlockStatePropertyCondition.hasBlockStateProperties(supplier.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(property, value));
    }
}