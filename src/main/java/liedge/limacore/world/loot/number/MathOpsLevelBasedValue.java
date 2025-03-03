package liedge.limacore.world.loot.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.math.MathOperation;
import liedge.limacore.registry.LimaCoreLootRegistries;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record MathOpsLevelBasedValue(LevelBasedValue first, LevelBasedValue second, MathOperation operation) implements LevelBasedValue
{
    public static final MapCodec<MathOpsLevelBasedValue> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("first").forGetter(MathOpsLevelBasedValue::first),
            LevelBasedValue.CODEC.fieldOf("second").forGetter(MathOpsLevelBasedValue::second),
            MathOperation.CODEC.fieldOf("op").forGetter(MathOpsLevelBasedValue::operation))
            .apply(instance, MathOpsLevelBasedValue::new));

    @Override
    public float calculate(int level)
    {
        return (float) operation.applyAsDouble(first.calculate(level), second.calculate(level));
    }

    @Override
    public MapCodec<? extends LevelBasedValue> codec()
    {
        return LimaCoreLootRegistries.MATH_OPS_LEVEL_BASED_VALUE.get();
    }
}