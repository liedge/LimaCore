package liedge.limacore.world.loot.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.lib.math.LimaRoundingMode;
import liedge.limacore.lib.math.MathOperation;
import liedge.limacore.util.LimaLootUtil;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.Set;

public record ValueMathOperation(NumberProvider first, NumberProvider second, MathOperation operation, LimaRoundingMode roundingMode) implements NumberProvider
{
    public static final MapCodec<ValueMathOperation> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviders.CODEC.fieldOf("first").forGetter(ValueMathOperation::first),
            NumberProviders.CODEC.fieldOf("second").forGetter(ValueMathOperation::second),
            MathOperation.SINGLE_OP_CODEC.fieldOf("op").forGetter(ValueMathOperation::operation),
            LimaRoundingMode.CODEC.optionalFieldOf("rounding_mode", LimaRoundingMode.NATURAL).forGetter(ValueMathOperation::roundingMode))
            .apply(instance, ValueMathOperation::new));

    public static NumberProvider of(NumberProvider first, NumberProvider second, MathOperation operation)
    {
        return new ValueMathOperation(first, second, operation, LimaRoundingMode.NATURAL);
    }

    @Override
    public float getFloat(LootContext context)
    {
        return (float) getDouble(context);
    }

    @Override
    public int getInt(LootContext context)
    {
        return LimaCoreMath.round(getDouble(context), roundingMode);
    }

    private double getDouble(LootContext context)
    {
        return operation.applyAsDouble(first.getFloat(context), second.getFloat(context));
    }

    @Override
    public MapCodec<? extends NumberProvider> codec()
    {
        return CODEC;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams()
    {
        return LimaLootUtil.joinReferencedParams(first, second);
    }
}