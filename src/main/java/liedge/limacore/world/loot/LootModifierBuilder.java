package liedge.limacore.world.loot;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;

import java.util.List;
import java.util.function.Function;

public abstract class LootModifierBuilder<M extends IGlobalLootModifier, B extends LootModifierBuilder<M, B>>
{
    public static SimpleBuilder<AddTableLootModifier> rollLootTable(ResourceKey<LootTable> lootTableKey)
    {
        return new SimpleBuilder<>(conditions -> new AddTableLootModifier(conditions, lootTableKey));
    }

    protected final List<LootItemCondition> conditions = new ObjectArrayList<>();

    @SuppressWarnings("unchecked")
    protected final B selfUnchecked()
    {
        return (B) this;
    }

    public B requires(LootItemCondition condition)
    {
        conditions.add(condition);
        return selfUnchecked();
    }

    public B requires(LootItemCondition.Builder builder)
    {
        return requires(builder.build());
    }

    public B killedByPlayer()
    {
        return requires(LootItemKilledByPlayerCondition.killedByPlayer());
    }

    public abstract M build();

    public static class SimpleBuilder<M extends IGlobalLootModifier> extends LootModifierBuilder<M, SimpleBuilder<M>>
    {
        private final Function<LootItemCondition[], M> factory;

        public SimpleBuilder(Function<LootItemCondition[], M> factory)
        {
            this.factory = factory;
        }

        @Override
        public M build()
        {
            return factory.apply(conditions.toArray(LootItemCondition[]::new));
        }
    }
}