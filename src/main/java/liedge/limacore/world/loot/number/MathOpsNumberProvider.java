package liedge.limacore.world.loot.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.math.MathOperation;
import liedge.limacore.registry.LimaCoreLootRegistries;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record MathOpsNumberProvider(NumberProvider first, NumberProvider second, MathOperation operation) implements NumberProvider
{
    public static final MapCodec<MathOpsNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviders.CODEC.fieldOf("first").forGetter(MathOpsNumberProvider::first),
            NumberProviders.CODEC.fieldOf("second").forGetter(MathOpsNumberProvider::second),
            MathOperation.CODEC.fieldOf("op").forGetter(MathOpsNumberProvider::operation))
            .apply(instance, MathOpsNumberProvider::new));

    public static NumberProvider of(NumberProvider first, NumberProvider second, MathOperation operation)
    {
        return new MathOpsNumberProvider(first, second, operation);
    }

    @Override
    public float getFloat(LootContext context)
    {
        return (float) operation.applyAsDouble(first.getFloat(context), second.getFloat(context));
    }

    @Override
    public int getInt(LootContext context)
    {
        return operation.applyAsInt(first.getInt(context), second.getInt(context));
    }

    @Override
    public LootNumberProviderType getType()
    {
        return LimaCoreLootRegistries.MATH_OPS_NUMBER_PROVIDER.get();
    }
}