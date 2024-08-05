package liedge.limacore.data.generation;

import liedge.limacore.lib.ModResources;
import liedge.limacore.world.generation.PlaceOnBlockFaceFeature;
import net.minecraft.core.*;
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

public abstract class LimaDatagenBootstrapBuilder
{
    public static DatapackBuiltinEntriesProvider createDataPackProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, String modid, LimaDatagenBootstrapBuilder builder)
    {
        builder.buildDataRegistryEntries(builder.builder);
        return new DatapackBuiltinEntriesProvider(packOutput, registries, builder.builder, Set.of(modid));
    }

    private final RegistrySetBuilder builder = new RegistrySetBuilder();

    protected abstract void buildDataRegistryEntries(RegistrySetBuilder builder);

    // Registration helpers
    protected void registerDamageType(BootstrapContext<DamageType> ctx, ResourceKey<DamageType> key, Function<String, DamageType> factory)
    {
        ctx.register(key, factory.apply(ModResources.translationKeyFromId(key.location())));
    }

    protected void registerDamageType(BootstrapContext<DamageType> ctx, ResourceKey<DamageType> key, DamageScaling scaling, float exhaustion, DamageEffects effects, DeathMessageType messageType)
    {
        registerDamageType(ctx, key, msgId -> new DamageType(msgId, scaling, exhaustion, effects, messageType));
    }

    protected void registerEnchantment(BootstrapContext<Enchantment> ctx, ResourceKey<Enchantment> key, Enchantment.Builder builder)
    {
        ctx.register(key, builder.build(key.location()));
    }

    // Commonly used data pack objects
    protected OreConfiguration oreConfig(int oreVeinSize, OreConfiguration.TargetBlockState... targetStates)
    {
        return new OreConfiguration(Arrays.asList(targetStates), oreVeinSize);
    }

    protected PlacedFeature orePlacement(Holder<ConfiguredFeature<?, ?>> configuration, int placementCount, PlacementModifier height)
    {
        return new PlacedFeature(configuration,
                List.of(CountPlacement.of(placementCount), InSquarePlacement.spread(), height, BiomeFilter.biome()));
    }

    protected OreConfiguration.TargetBlockState singleBlockOreTarget(Block targetBlock, Supplier<? extends Block> oreBlock)
    {
        return OreConfiguration.target(new BlockMatchTest(targetBlock), oreBlock.get().defaultBlockState());
    }

    protected OreConfiguration.TargetBlockState tagMatchOreTarget(TagKey<Block> targetTag, Supplier<? extends Block> oreBlock)
    {
        return OreConfiguration.target(new TagMatchTest(targetTag), oreBlock.get().defaultBlockState());
    }

    protected PlaceOnBlockFaceFeature.TargetBlockState blockFaceTarget(Block targetBlock, Block toPlace, Direction face)
    {
        return new PlaceOnBlockFaceFeature.TargetBlockState(new BlockMatchTest(targetBlock), face, toPlace.defaultBlockState());
    }

    protected PlaceOnBlockFaceFeature.TargetBlockState blockFaceTarget(Block targetBlock, Supplier<? extends Block> toPlaceSupplier, Direction face)
    {
        return blockFaceTarget(targetBlock, toPlaceSupplier.get(), face);
    }

    protected PlaceOnBlockFaceFeature.TargetBlockState blockFaceTargetAutoOrient(Block targetBlock, Block toPlace, Direction face)
    {
        return new PlaceOnBlockFaceFeature.TargetBlockState(new BlockMatchTest(targetBlock), face, toPlace.defaultBlockState().setValue(BlockStateProperties.FACING, face));
    }

    protected PlaceOnBlockFaceFeature.TargetBlockState blockFaceTargetAutoOrient(Block targetBlock, Supplier<? extends Block> toPlaceSupplier, Direction face)
    {
        return new PlaceOnBlockFaceFeature.TargetBlockState(new BlockMatchTest(targetBlock), face, toPlaceSupplier.get().defaultBlockState().setValue(BlockStateProperties.FACING, face));
    }
}