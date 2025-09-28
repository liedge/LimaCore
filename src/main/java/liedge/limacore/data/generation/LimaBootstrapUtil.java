package liedge.limacore.data.generation;

import liedge.limacore.lib.ModResources;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class LimaBootstrapUtil
{
    private LimaBootstrapUtil() {}

    // Provider helper
    public static DatapackBuiltinEntriesProvider createDataPackProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> baseRegistries, String modid, UnaryOperator<RegistrySetBuilder> builderOp)
    {
        RegistrySetBuilder builder = builderOp.apply(new RegistrySetBuilder());
        return new DatapackBuiltinEntriesProvider(packOutput, baseRegistries, builder, Set.of(modid));
    }

    // Damage type helpers
    public static void registerDamageType(BootstrapContext<DamageType> context, ResourceKey<DamageType> key, Function<String, DamageType> fromNameConstructor)
    {
        context.register(key, fromNameConstructor.apply(ModResources.idLangKey(key.location())));
    }

    public static void registerDamageType(BootstrapContext<DamageType> context, ResourceKey<DamageType> key, DamageScaling scaling, float exhaustion, DamageEffects effects, DeathMessageType messageType)
    {
        registerDamageType(context, key, name -> new DamageType(name, scaling, exhaustion, effects, messageType));
    }

    // Enchantment
    public static void registerEnchantment(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder)
    {
        context.register(key, builder.build(key.location()));
    }

    // Commonly used objects
    public static BlockStateProvider simpleState(Holder<Block> holder)
    {
        return BlockStateProvider.simple(holder.value());
    }

    public static BlockPredicate matchesBlocks(Direction side, Block... blocks)
    {
        return BlockPredicate.matchesBlocks(side.getNormal(), blocks);
    }

    public static BlockPredicate isAir(Direction side)
    {
        return matchesBlocks(side, Blocks.AIR);
    }

    public static BlockPredicate isAirOrWater(Direction side)
    {
        return matchesBlocks(side, Blocks.AIR, Blocks.WATER);
    }

    public static BlockPredicate replaceable(Direction side)
    {
        return BlockPredicate.replaceable(side.getNormal());
    }

    public static BlockPredicate noFluids(Direction side)
    {
        return BlockPredicate.noFluid(side.getNormal());
    }

    private static BlockPredicate replaceableSturdyFace(Direction side, boolean allowFluids)
    {
        BlockPredicate sturdy = BlockPredicate.hasSturdyFace(side);
        BlockPredicate replaceable = replaceable(side);

        return allowFluids ? BlockPredicate.allOf(sturdy, replaceable) : BlockPredicate.allOf(sturdy, replaceable, noFluids(side));
    }

    public static BlockPredicate replaceableSturdyFaces(boolean allowFluids, Direction... sides)
    {
        List<BlockPredicate> predicates = Arrays.stream(sides).map(o -> replaceableSturdyFace(o, allowFluids)).toList();
        return BlockPredicate.anyOf(predicates);
    }

    public static BlockPredicate replaceableSturdyFaces(Direction... sides)
    {
        return replaceableSturdyFaces(true, sides);
    }

    public static BlockPredicate replaceableSturdyFaces(boolean allowFluids)
    {
        return replaceableSturdyFaces(allowFluids, Direction.values());
    }

    public static BlockPredicate replaceableSturdyFaces()
    {
        return replaceableSturdyFaces(true);
    }

    public static OreConfiguration oreConfig(int oreVeinSize, OreConfiguration.TargetBlockState... targetStates)
    {
        return new OreConfiguration(Arrays.asList(targetStates), oreVeinSize);
    }

    public static PlacedFeature orePlacement(Holder<ConfiguredFeature<?, ?>> configuration, int placementCount, PlacementModifier height)
    {
        return new PlacedFeature(configuration,
                List.of(CountPlacement.of(placementCount), InSquarePlacement.spread(), height, BiomeFilter.biome()));
    }

    public static OreConfiguration.TargetBlockState singleBlockOreTarget(Block targetBlock, Supplier<? extends Block> oreBlock)
    {
        return OreConfiguration.target(new BlockMatchTest(targetBlock), oreBlock.get().defaultBlockState());
    }

    public static OreConfiguration.TargetBlockState tagMatchOreTarget(TagKey<Block> targetTag, Supplier<? extends Block> oreBlock)
    {
        return OreConfiguration.target(new TagMatchTest(targetTag), oreBlock.get().defaultBlockState());
    }
}