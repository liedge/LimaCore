package liedge.limacore.world.loot.condition;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.math.CompareOperation;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.Set;

public record NumberComparisonLootCondition(NumberProvider first, NumberProvider second, CompareOperation operation) implements LootItemCondition
{
    public static final MapCodec<NumberComparisonLootCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviders.CODEC.fieldOf("first").forGetter(NumberComparisonLootCondition::first),
            NumberProviders.CODEC.fieldOf("second").forGetter(NumberComparisonLootCondition::second),
            CompareOperation.CODEC.fieldOf("operation").forGetter(NumberComparisonLootCondition::operation))
            .apply(instance, NumberComparisonLootCondition::new));

    public static LootItemCondition.Builder comparingValues(NumberProvider first, NumberProvider second, CompareOperation operation)
    {
        return () -> new NumberComparisonLootCondition(first, second, operation);
    }

    @Override
    public LootItemConditionType getType()
    {
        return LimaCoreLootRegistries.NUMBER_COMPARISON_CONDITION.get();
    }

    @Override
    public boolean test(LootContext context)
    {
        return operation().test(first.getFloat(context), second.getFloat(context));
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return Sets.union(first.getReferencedContextParams(), second.getReferencedContextParams());
    }
}