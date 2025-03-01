package liedge.limacore.util;

import liedge.limacore.world.loot.EnchantmentLevelSubPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.predicates.ValueCheckCondition;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

public final class LimaLootUtil
{
    private LimaLootUtil() {}

    // Loot condition helpers
    public static LootItemCondition.Builder randomChanceLinearEnchantBonus(Holder<Enchantment> enchantment, float baseChance, float perLevelAfterFirst)
    {
        return () -> new LootItemRandomChanceWithEnchantedBonusCondition(baseChance, new LevelBasedValue.Linear(baseChance + perLevelAfterFirst, perLevelAfterFirst), enchantment);
    }

    public static LootItemCondition.Builder randomChanceLinearEnchantBonus(HolderLookup.Provider registries, ResourceKey<Enchantment> enchantmentKey, float baseChance, float perLevelAfterFirst)
    {
        return randomChanceLinearEnchantBonus(registries.holderOrThrow(enchantmentKey), baseChance, perLevelAfterFirst);
    }

    public static LootItemCondition.Builder specificLootTable(ResourceKey<LootTable> lootTableKey)
    {
        return LootTableIdCondition.builder(lootTableKey.location());
    }

    public static LootItemCondition.Builder blockLootTable(Block block)
    {
        return specificLootTable(block.getLootTable());
    }

    public static LootItemCondition.Builder blockLootTable(Holder<Block> holder)
    {
        return blockLootTable(holder.value());
    }

    public static LootItemCondition.Builder defaultEntityLootTable(EntityType<?> type)
    {
        return specificLootTable(type.getDefaultLootTable());
    }

    public static LootItemCondition.Builder defaultEntityLootTable(Holder<EntityType<?>> holder)
    {
        return defaultEntityLootTable(holder.value());
    }

    public static LootItemCondition.Builder needsEntityType(EntityType<?> type)
    {
        return LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().of(type));
    }

    public static NumberProvider linearEnchantmentLevel()
    {
        return EnchantmentLevelProvider.forEnchantmentLevel(LevelBasedValue.perLevel(1));
    }

    public static LootItemCondition.Builder contextEnchantmentLevels(IntRange validLevels)
    {
        return ValueCheckCondition.hasValue(linearEnchantmentLevel(), validLevels);
    }

    public static LootItemCondition.Builder entityEnchantmentLevels(LootContext.EntityTarget entityTarget, EnchantmentLevelSubPredicate predicate)
    {
        return LootItemEntityPropertyCondition.hasProperties(entityTarget, EntityPredicate.Builder.entity().subPredicate(predicate));
    }
}