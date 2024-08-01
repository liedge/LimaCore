package liedge.limacore.data.generation.loot;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public interface LimaLootSubProviderExtensions extends LootTableSubProvider
{
    default LootTable.Builder singlePoolTable(LootPool.Builder pool)
    {
        return LootTable.lootTable().withPool(pool);
    }

    default LootTable.Builder singleItemTable(ItemLike item)
    {
        return LootTable.lootTable().withPool(singleItemPool(item));
    }

    default LootTable.Builder singleItemTable(LootPoolSingletonContainer.Builder<?> lootItem)
    {
        return LootTable.lootTable().withPool(singleItemPool(lootItem));
    }

    default LootPool.Builder singleItemPool(LootPoolSingletonContainer.Builder<?> lootItem)
    {
        return LootPool.lootPool().add(lootItem).setRolls(ConstantValue.exactly(1));
    }

    default LootPool.Builder singleItemPool(ItemLike item)
    {
        return singleItemPool(lootItem(item));
    }

    default LootPoolSingletonContainer.Builder<?> lootItem(ItemLike item)
    {
        return LootItem.lootTableItem(item);
    }

    // Helper condition/function builders
    default LootItemCondition.Builder randomChanceAndEnchantmentBoost(HolderLookup.Provider registries, ResourceKey<Enchantment> enchantmentKey, float baseChance, float perLevelAfterFirst)
    {
        return randomChanceAndEnchantmentBoost(registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantmentKey), baseChance, perLevelAfterFirst);
    }

    default LootItemCondition.Builder randomChanceAndEnchantmentBoost(Holder<Enchantment> enchantment, float baseChance, float perLevelAfterFirst)
    {
        return () -> new LootItemRandomChanceWithEnchantedBonusCondition(baseChance, new LevelBasedValue.Linear(baseChance + perLevelAfterFirst, perLevelAfterFirst), enchantment);
    }
}