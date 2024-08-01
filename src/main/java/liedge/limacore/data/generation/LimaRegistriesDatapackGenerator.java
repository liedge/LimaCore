package liedge.limacore.data.generation;

import com.mojang.serialization.Lifecycle;
import liedge.limacore.world.generation.PlaceOnBlockFaceFeature;
import net.minecraft.core.*;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class LimaRegistriesDatapackGenerator
{
    private final RegistrySetBuilder builder = new RegistrySetBuilder();
    private final ExistingFileHelper helper;

    protected LimaRegistriesDatapackGenerator(ExistingFileHelper helper)
    {
        this.helper = helper;
    }

    public DatapackBuiltinEntriesProvider buildProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modid)
    {
        createAllDataObjects();
        return new DatapackBuiltinEntriesProvider(output, registries, builder, Set.of(modid));
    }

    protected abstract void createAllDataObjects();

    protected <T> void createDataFor(ResourceKey<? extends Registry<T>> registryKey, Consumer<BootstrapContext<T>> consumer)
    {
        builder.add(registryKey, ctx -> {
            BootstrapContext<T> wrapped = trackingContext(registryKey, ctx);
            consumer.accept(wrapped);
        });
    }

    private <T> BootstrapContext<T> trackingContext(ResourceKey<? extends Registry<T>> registryKey, BootstrapContext<T> ctx)
    {
        return new BootstrapContext<>()
        {
            private final ExistingFileHelper.IResourceType resourceType = new ExistingFileHelper.ResourceType(PackType.SERVER_DATA, ".json", CommonHooks.prefixNamespace(registryKey.location()));

            @Override
            public Holder.Reference<T> register(ResourceKey<T> valueKey, T value, Lifecycle lifecycle)
            {
                helper.trackGenerated(valueKey.location(), resourceType);
                return ctx.register(valueKey, value, lifecycle);
            }

            @Override
            public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> registryKey)
            {
                return ctx.lookup(registryKey);
            }

            @Override
            public <S> Optional<HolderLookup.RegistryLookup<S>> registryLookup(ResourceKey<? extends Registry<? extends S>> registry)
            {
                return ctx.registryLookup(registry);
            }
        };
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