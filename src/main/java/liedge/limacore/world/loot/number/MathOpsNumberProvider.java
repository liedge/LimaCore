package liedge.limacore.world.loot.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.math.LimaRoundingMode;
import liedge.limacore.lib.math.MathOperation;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import liedge.limacore.lib.math.LimaCoreMath;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record MathOpsNumberProvider(NumberProvider first, NumberProvider second, MathOperation operation, LimaRoundingMode roundingMode) implements NumberProvider
{
    public static final MapCodec<MathOpsNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviders.CODEC.fieldOf("first").forGetter(MathOpsNumberProvider::first),
            NumberProviders.CODEC.fieldOf("second").forGetter(MathOpsNumberProvider::second),
            MathOperation.SINGLE_OP_CODEC.fieldOf("op").forGetter(MathOpsNumberProvider::operation),
            LimaRoundingMode.CODEC.optionalFieldOf("rounding_mode", LimaRoundingMode.NATURAL).forGetter(MathOpsNumberProvider::roundingMode))
            .apply(instance, MathOpsNumberProvider::new));

    public static NumberProvider of(NumberProvider first, NumberProvider second, MathOperation operation)
    {
        return new MathOpsNumberProvider(first, second, operation, LimaRoundingMode.NATURAL);
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
    public LootNumberProviderType getType()
    {
        return LimaCoreLootRegistries.MATH_OPS_NUMBER_PROVIDER.get();
    }
}