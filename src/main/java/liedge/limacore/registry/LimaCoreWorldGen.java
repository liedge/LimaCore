package liedge.limacore.registry;

import liedge.limacore.LimaCore;
import liedge.limacore.world.generation.*;
import net.minecraft.core.registries.Registries;
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

    public static void initRegister(IEventBus bus)
    {
        FEATURES.register(bus);
        PLACEMENT_MODIFIERS.register(bus);
    }

    // Features
    public static final DeferredHolder<Feature<?>, PlaceOnBlockFaceFeature> PLACE_ON_BLOCK_FACE = FEATURES.register("place_on_block_face", PlaceOnBlockFaceFeature::new);

    // Placement modifiers
    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<OnSurfaceHeightRangePlacement>> ON_SURFACE_HEIGHT_RANGE = PLACEMENT_MODIFIERS.register("on_surface_height_range", () -> new LimaPlacementType<>(OnSurfaceHeightRangePlacement.MAP_CODEC));
    public static final DeferredHolder<PlacementModifierType<?>, LimaPlacementType<StructurePlacementFilter>> STRUCTURE_PLACEMENT = PLACEMENT_MODIFIERS.register("structure_placement", () -> new LimaPlacementType<>(StructurePlacementFilter.MAP_CODEC));
}