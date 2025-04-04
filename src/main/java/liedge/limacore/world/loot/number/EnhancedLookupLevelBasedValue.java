package liedge.limacore.world.loot.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatList;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.List;

public record EnhancedLookupLevelBasedValue(List<Float> values, int levelOffset, float defaultBelow, float defaultAbove) implements LevelBasedValue
{
    public static final MapCodec<EnhancedLookupLevelBasedValue> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.listOf().fieldOf("values").forGetter(EnhancedLookupLevelBasedValue::values),
            Codec.intRange(1, 255).optionalFieldOf("level_offset", 1).forGetter(EnhancedLookupLevelBasedValue::levelOffset),
            Codec.FLOAT.fieldOf("default_below").forGetter(EnhancedLookupLevelBasedValue::defaultBelow),
            Codec.FLOAT.fieldOf("default_above").forGetter(EnhancedLookupLevelBasedValue::defaultAbove))
            .apply(instance, EnhancedLookupLevelBasedValue::new));

    public static LevelBasedValue standardLookup(float defaultBelow, float defaultAbove, float... values)
    {
        return new EnhancedLookupLevelBasedValue(FloatList.of(values), 1, defaultBelow, defaultAbove);
    }

    public static LevelBasedValue offsetLookup(int levelOffset, float defaultBelow, float defaultAbove, float... values)
    {
        return new EnhancedLookupLevelBasedValue(FloatList.of(values), levelOffset, defaultBelow, defaultAbove);
    }

    @Override
    public float calculate(int level)
    {
        if (level < levelOffset)
        {
            return defaultBelow;
        }
        else
        {
            int n = level - levelOffset;
            return n < values.size() ? values.get(n) : defaultAbove;
        }
    }

    @Override
    public MapCodec<? extends LevelBasedValue> codec()
    {
        return LimaCoreLootRegistries.ENHANCED_LOOKUP_LEVEL_BASED_VALUE.get();
    }
}