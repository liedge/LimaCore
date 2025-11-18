package liedge.limacore.util;

import liedge.limacore.advancement.EnchantmentLevelEntityPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.predicates.ValueCheckCondition;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class LimaLootUtil
{
    private LimaLootUtil() {}

    //#region Loot context helper factories
    public static LootContext emptyLootContext(ServerLevel level)
    {
        LootParams params = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
        return new LootContext.Builder(params).create(Optional.empty());
    }

    public static LootContext chestLootContext(ServerLevel level, Entity target, @Nullable Entity attacker)
    {
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, target.position())
                .withOptionalParameter(LootContextParams.THIS_ENTITY, target)
                .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, attacker)
                .create(LootContextParamSets.CHEST);
        return new LootContext.Builder(params).create(Optional.empty());
    }

    public static LootContext entityLootContext(ServerLevel level, Entity target, DamageSource damageSource, LivingEntity attacker)
    {
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, target)
                .withParameter(LootContextParams.ORIGIN, target.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, damageSource)
                .withParameter(LootContextParams.ATTACKING_ENTITY, attacker)
                .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, damageSource.getDirectEntity())
                .create(LootContextParamSets.ENTITY);
        return new LootContext.Builder(params).create(Optional.empty());
    }

    //#endregion

    // Loot condition helpers
    public static LootItemCondition.Builder randomChanceWithEnchantBonus(Holder<Enchantment> enchantment, float nonEnchantedChance, LevelBasedValue enchantedChance)
    {
        return () -> new LootItemRandomChanceWithEnchantedBonusCondition(nonEnchantedChance, enchantedChance, enchantment);
    }

    public static LootItemCondition.Builder randomChanceLinearEnchantBonus(Holder<Enchantment> enchantment, float baseChance, float perLevelAfterFirst)
    {
        return randomChanceWithEnchantBonus(enchantment, baseChance, new LevelBasedValue.Linear(baseChance + perLevelAfterFirst, perLevelAfterFirst));
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

    public static LootItemCondition.Builder needsEntityTag(TagKey<EntityType<?>> tagKey)
    {
        return LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().of(tagKey));
    }

    public static NumberProvider linearEnchantmentLevel()
    {
        return EnchantmentLevelProvider.forEnchantmentLevel(LevelBasedValue.perLevel(1));
    }

    public static LootItemCondition.Builder contextEnchantmentLevels(IntRange validLevels)
    {
        return ValueCheckCondition.hasValue(linearEnchantmentLevel(), validLevels);
    }

    public static LootItemCondition.Builder entityEnchantmentLevels(LootContext.EntityTarget entityTarget, EnchantmentLevelEntityPredicate predicate)
    {
        return LootItemEntityPropertyCondition.hasProperties(entityTarget, EntityPredicate.Builder.entity().subPredicate(predicate));
    }
}