package liedge.limacore.world.loot.level;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatList;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.List;
import java.util.Optional;

public record RangedLookupLevelBasedValue(List<Float> values, int levelOffset, Optional<Float> defaultBelow, Optional<Float> defaultAbove) implements LevelBasedValue
{
    public static final MapCodec<RangedLookupLevelBasedValue> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.nonEmptyList(Codec.FLOAT.listOf()).fieldOf("values").forGetter(RangedLookupLevelBasedValue::values),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("level_offset", 0).forGetter(RangedLookupLevelBasedValue::levelOffset),
            Codec.FLOAT.optionalFieldOf("default_below").forGetter(RangedLookupLevelBasedValue::defaultBelow),
            Codec.FLOAT.optionalFieldOf("default_above").forGetter(RangedLookupLevelBasedValue::defaultAbove))
            .apply(instance, RangedLookupLevelBasedValue::new));

    public static RangedLookupLevelBasedValue create(int startingLevel, float defaultBelow, float defaultAbove, float... values)
    {
        Preconditions.checkArgument(startingLevel > 0, "Ranged lookup table level range must start at 1.");
        return new RangedLookupLevelBasedValue(FloatList.of(values), startingLevel - 1, Optional.of(defaultBelow), Optional.of(defaultAbove));
    }

    public static RangedLookupLevelBasedValue lookupStartingAtLevel(int startingLevel, float... values)
    {
        Preconditions.checkArgument(startingLevel > 0, "Ranged lookup table level range must start at 1.");
        return new RangedLookupLevelBasedValue(FloatList.of(values), startingLevel - 1, Optional.empty(), Optional.empty());
    }

    public static RangedLookupLevelBasedValue lookup(float... values)
    {
        return lookupStartingAtLevel(1, values);
    }

    @Override
    public float calculate(int level)
    {
        int shiftedLevel = level - 1 - levelOffset;  // Level is 1-based rather than 0, adjust accordingly.

        if (shiftedLevel < 0)
            return defaultBelow.orElse(values.getFirst());
        else if (shiftedLevel >= values.size())
            return defaultAbove.orElse(values.getLast());
        else
            return values.get(shiftedLevel);
    }

    @Override
    public MapCodec<? extends LevelBasedValue> codec()
    {
        return LimaCoreLootRegistries.RANGED_LOOKUP_LEVEL_BASED_VALUE.get();
    }
}