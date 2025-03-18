package liedge.limacore.data.generation;

import liedge.limacore.lib.ModResources;
import liedge.limacore.world.generation.PlaceOnBlockFaceFeature;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
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
        context.register(key, fromNameConstructor.apply(ModResources.translationKeyFromId(key.location())));
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

    public static PlaceOnBlockFaceFeature.TargetBlockState blockFaceTarget(Block targetBlock, Block toPlace, Direction face)
    {
        return new PlaceOnBlockFaceFeature.TargetBlockState(new BlockMatchTest(targetBlock), face, toPlace.defaultBlockState());
    }

    public static PlaceOnBlockFaceFeature.TargetBlockState blockFaceTarget(Block targetBlock, Supplier<? extends Block> toPlaceSupplier, Direction face)
    {
        return blockFaceTarget(targetBlock, toPlaceSupplier.get(), face);
    }

    public static PlaceOnBlockFaceFeature.TargetBlockState blockFaceTargetAutoOrient(Block targetBlock, Block toPlace, Direction face)
    {
        return new PlaceOnBlockFaceFeature.TargetBlockState(new BlockMatchTest(targetBlock), face, toPlace.defaultBlockState().setValue(BlockStateProperties.FACING, face));
    }

    public static PlaceOnBlockFaceFeature.TargetBlockState blockFaceTargetAutoOrient(Block targetBlock, Supplier<? extends Block> toPlaceSupplier, Direction face)
    {
        return new PlaceOnBlockFaceFeature.TargetBlockState(new BlockMatchTest(targetBlock), face, toPlaceSupplier.get().defaultBlockState().setValue(BlockStateProperties.FACING, face));
    }
}