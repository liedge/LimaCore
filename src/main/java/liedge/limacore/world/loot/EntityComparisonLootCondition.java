package liedge.limacore.world.loot;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Set;

public abstract class EntityComparisonLootCondition implements LootItemCondition
{
    protected static <T extends EntityComparisonLootCondition> Products.P2<RecordCodecBuilder.Mu<T>, LootContext.EntityTarget, LootContext.EntityTarget> commonFields(RecordCodecBuilder.Instance<T> instance)
    {
        return instance.group(
                LootContext.EntityTarget.CODEC.fieldOf("first").forGetter(o -> o.first),
                LootContext.EntityTarget.CODEC.fieldOf("second").forGetter(o -> o.second));
    }

    final LootContext.EntityTarget first;
    final LootContext.EntityTarget second;

    protected EntityComparisonLootCondition(LootContext.EntityTarget first, LootContext.EntityTarget second)
    {
        this.first = first;
        this.second = second;
    }

    protected abstract boolean testEntities(Entity firstEntity, Entity secondEntity);

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return Set.of(first.getParam(), second.getParam());
    }

    @Override
    public final boolean test(LootContext context)
    {
        Entity firstEntity = context.getParamOrNull(first.getParam());
        Entity secondEntity = context.getParamOrNull(second.getParam());

        return firstEntity != null && secondEntity != null && testEntities(firstEntity, secondEntity);
    }
}