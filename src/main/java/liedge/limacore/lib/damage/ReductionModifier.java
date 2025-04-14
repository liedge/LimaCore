package liedge.limacore.lib.damage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.math.MathOperation;

public record ReductionModifier(float amount, MathOperation operation, DamageReductionType reductionType)
{
    public static final Codec<ReductionModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("amount").forGetter(ReductionModifier::amount),
            MathOperation.CODEC.fieldOf("op").forGetter(ReductionModifier::operation),
            DamageReductionType.CODEC.fieldOf("type").forGetter(ReductionModifier::reductionType))
            .apply(instance, ReductionModifier::new));
}