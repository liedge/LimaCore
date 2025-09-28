package liedge.limacore.registry.game;

import liedge.limacore.LimaCore;
import liedge.limacore.world.generation.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaCoreWorldGen
{
    private LimaCoreWorldGen() {}

    private static final DeferredRegister<Feature<?>> FEATURES = LimaCore.RESOURCES.deferredRegister(Registries.FEATURE);
    private static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS = LimaCore.RESOURCES.deferredRegister(Registries.PLACEMENT_MODIFIER_TYPE);
    private static final DeferredRegister<BlockPredicateType<?>> BLOCK_PREDICATES = LimaCore.RESOURCES.deferredRegister(Registries.BLOCK_PREDICATE_TYPE);

    public static void register(IEventBus bus)
    {
        FEATURES.register(bus);
        PLACEMENT_MODIFIERS.register(bus);
        BLOCK_PREDICATES.register(bus);
    }

    // Features
    public static final DeferredHolder<Feature<?>, PlaceOnSideFeature> PLACE_ON_SIDE_FEATURE = FEATURES.register("place_on_side", PlaceOnSideFeature::new);

    // Placement modifiers
    public static final DeferredHolder<PlacementModifierType<?>, LimaPlacementType<RandomAxisScanPlacement>> RANDOM_AXIS_SCAN_PLACEMENT = PLACEMENT_MODIFIERS.register("random_axis_scan", id -> new LimaPlacementType<>(id, RandomAxisScanPlacement.CODEC));
    public static final DeferredHolder<PlacementModifierType<?>, LimaPlacementType<StructurePlacementFilter>> STRUCTURE_PLACEMENT = PLACEMENT_MODIFIERS.register("in_structure", id -> new LimaPlacementType<>(id, StructurePlacementFilter.MAP_CODEC));

    // Block predicates
    public static final DeferredHolder<BlockPredicateType<?>, LimaBlockPredicateType<SkyExposureBlockPredicate>> SKY_EXPOSURE_PREDICATE = BLOCK_PREDICATES.register("sky_exposure", id -> new LimaBlockPredicateType<>(id, SkyExposureBlockPredicate.CODEC));
}