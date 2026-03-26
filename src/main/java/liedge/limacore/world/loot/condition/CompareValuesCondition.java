package liedge.limacore.world.loot.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.math.CompareOperation;
import liedge.limacore.util.LimaLootUtil;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.Set;

public record CompareValuesCondition(NumberProvider first, NumberProvider second, CompareOperation operation) implements LootItemCondition
{
    public static final MapCodec<CompareValuesCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviders.CODEC.fieldOf("first").forGetter(CompareValuesCondition::first),
            NumberProviders.CODEC.fieldOf("second").forGetter(CompareValuesCondition::second),
            CompareOperation.CODEC.fieldOf("operation").forGetter(CompareValuesCondition::operation))
            .apply(instance, CompareValuesCondition::new));

    public static LootItemCondition.Builder comparingValues(NumberProvider first, NumberProvider second, CompareOperation operation)
    {
        return () -> new CompareValuesCondition(first, second, operation);
    }

    @Override
    public MapCodec<? extends LootItemCondition> codec()
    {
        return CODEC;
    }

    @Override
    public boolean test(LootContext context)
    {
        return operation().test(first.getFloat(context), second.getFloat(context));
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams()
    {
        return LimaLootUtil.joinReferencedParams(first, second);
    }
}