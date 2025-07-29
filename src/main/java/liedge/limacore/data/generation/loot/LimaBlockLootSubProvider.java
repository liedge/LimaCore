package liedge.limacore.data.generation.loot;

import liedge.limacore.util.LimaRegistryUtil;
import liedge.limacore.world.loot.SaveBlockEntityFunction;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
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

    protected void add(Holder<Block> holder, LootTable.Builder builder)
    {
        add(holder.value(), builder);
    }

    protected void add(Holder<Block> holder, Function<Block, LootTable.Builder> factory)
    {
        add(holder.value(), factory);
    }

    protected void dropSelf(Holder<Block> holder)
    {
        dropSelf(holder.value());
    }

    protected void dropSelf(Collection<? extends Holder<Block>> holders)
    {
        holders.forEach(this::dropSelf);
    }

    @SafeVarargs
    protected final void dropSelf(Holder<Block>... holders)
    {
        Stream.of(holders).forEach(this::dropSelf);
    }

    protected void dropSelfWithEntity(Block block)
    {
        add(block, singlePoolTable(applyExplosionCondition(block, singleItemPool(block)).apply(SaveBlockEntityFunction.saveBlockEntityData())));
    }

    protected void dropSelfWithEntity(Holder<Block> holder)
    {
        dropSelfWithEntity(holder.value());
    }

    protected void oreDrop(Block oreBlock, ItemLike rawOreItem)
    {
        add(oreBlock, createOreDrop(oreBlock, rawOreItem.asItem()));
    }

    protected void oreDrop(Holder<Block> oreBlockHolder, ItemLike rawOreItem)
    {
        oreDrop(oreBlockHolder.value(), rawOreItem);
    }

    protected <T extends Comparable<T> & StringRepresentable> LootItemCondition.Builder matchStateProperty(Block block, Property<T> property, T value)
    {
        return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(property, value));
    }

    protected <T extends Comparable<T> & StringRepresentable> LootItemCondition.Builder matchStateProperty(Holder<Block> holder, Property<T> property, T value)
    {
        return matchStateProperty(holder.value(), property, value);
    }
}