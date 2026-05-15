package liedge.limacore.world.loot.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Set;

public record MatchDefaultLootTable(LootContext.EntityTarget target) implements LootItemCondition
{
    public static final MapCodec<MatchDefaultLootTable> CODEC = LootContext.EntityTarget.CODEC.fieldOf("target").xmap(MatchDefaultLootTable::new, MatchDefaultLootTable::target);

    public static LootItemCondition.Builder matchesDefaultLootTable(LootContext.EntityTarget target)
    {
        return () -> new  MatchDefaultLootTable(target);
    }

    public static LootItemCondition.Builder matchesDefaultLootTable()
    {
        return matchesDefaultLootTable(LootContext.EntityTarget.THIS);
    }

    @Override
    public MapCodec<? extends LootItemCondition> codec()
    {
        return CODEC;
    }

    @Override
    public boolean test(LootContext context)
    {
        Entity entity = context.getOptionalParameter(target.contextParam());
        if (entity == null) return false;

        ResourceKey<LootTable> key = entity.getLootTable().orElse(null);
        return key != null && context.getQueriedLootTableId().equals(key.identifier());
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams()
    {
        return Set.of(target.contextParam());
    }
}