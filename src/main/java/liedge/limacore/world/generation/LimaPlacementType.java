package liedge.limacore.world.generation;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public record LimaPlacementType<T extends PlacementModifier>(MapCodec<T> codec) implements PlacementModifierType<T> {}