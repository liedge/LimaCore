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

    private final List<LootItemCondition> conditions = new ObjectArrayList<>();

    @SuppressWarnings("unchecked")
    protected final B selfUnchecked()
    {
        return (B) this;
    }

    protected LootItemCondition[] buildConditions()
    {
        return conditions.toArray(LootItemCondition[]::new);
    }

    protected abstract M createModifier(LootItemCondition[] conditions);

    public final M build()
    {
        return createModifier(conditions.toArray(LootItemCondition[]::new));
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

    public static class SimpleBuilder<M extends IGlobalLootModifier> extends LootModifierBuilder<M, SimpleBuilder<M>>
    {
        private final Function<LootItemCondition[], M> factory;

        public SimpleBuilder(Function<LootItemCondition[], M> factory)
        {
            this.factory = factory;
        }

        @Override
        protected M createModifier(LootItemCondition[] conditions)
        {
            return factory.apply(conditions);
        }
    }
}