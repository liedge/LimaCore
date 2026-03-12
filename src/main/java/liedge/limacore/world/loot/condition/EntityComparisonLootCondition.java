package liedge.limacore.world.loot.condition;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
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

    protected abstract boolean testEntities(ServerLevel level, Entity firstEntity, Entity secondEntity);

    @Override
    public Set<ContextKey<?>> getReferencedContextParams()
    {
        return Set.of(first.contextParam(), second.contextParam());
    }

    @Override
    public final boolean test(LootContext context)
    {
        Entity firstEntity = context.getOptionalParameter(first.contextParam());
        Entity secondEntity = context.getOptionalParameter(second.contextParam());

        return firstEntity != null && secondEntity != null && testEntities(context.getLevel(), firstEntity, secondEntity);
    }
}