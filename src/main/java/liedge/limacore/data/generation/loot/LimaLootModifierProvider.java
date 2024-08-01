package liedge.limacore.data.generation.loot;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.ModResources;
import liedge.limacore.world.loot.EnchantmentBasedEntityTableLootModifier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class LimaLootModifierProvider extends GlobalLootModifierProvider
{
    protected LimaLootModifierProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, ModResources resources)
    {
        super(packOutput, registries, resources.modid());
    }

    protected <T extends IGlobalLootModifier> ModifierBuilder<T> getBuilder(Function<LootItemCondition[], T> factory)
    {
        return new ModifierBuilder<>(factory);
    }

    protected ModifierBuilder<AddTableLootModifier> rollTable(ResourceKey<LootTable> lootTableKey)
    {
        return getBuilder(conditions -> new AddTableLootModifier(conditions, lootTableKey));
    }

    protected ModifierBuilder<EnchantmentBasedEntityTableLootModifier> rollEnchantmentTableForAllEntities(Holder<Enchantment> enchantment)
    {
        return getBuilder(conditions -> new EnchantmentBasedEntityTableLootModifier(conditions, enchantment));
    }

    public class ModifierBuilder<T extends IGlobalLootModifier>
    {
        private final List<LootItemCondition> conditions = new ObjectArrayList<>();
        private final Function<LootItemCondition[], T> factory;

        private ModifierBuilder(Function<LootItemCondition[], T> factory)
        {
            this.factory = factory;
        }

        public ModifierBuilder<T> requires(LootItemCondition condition)
        {
            conditions.add(condition);
            return this;
        }

        public ModifierBuilder<T> requires(LootItemCondition.Builder builder)
        {
            return requires(builder.build());
        }

        public ModifierBuilder<T> killedByPlayer()
        {
            return requires(LootItemKilledByPlayerCondition.killedByPlayer());
        }

        public ModifierBuilder<T> forEntity(EntityType<?> entityType)
        {
            return requires(LootTableIdCondition.builder(entityType.getDefaultLootTable().location()));
        }

        public ModifierBuilder<T> forBlock(Block block)
        {
            return requires(LootTableIdCondition.builder(block.getLootTable().location()));
        }

        public void build(String name)
        {
            LootItemCondition[] conditionArray = conditions.toArray(LootItemCondition[]::new);
            add(name, factory.apply(conditionArray), List.of());
        }
    }
}