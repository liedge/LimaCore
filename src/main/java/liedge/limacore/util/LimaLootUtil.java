package liedge.limacore.util;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import liedge.limacore.advancement.EnchantmentLevelEntityPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.predicates.ValueCheckCondition;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public final class LimaLootUtil
{
    private LimaLootUtil() {}

    public static <T extends LootContextUser> Codec<T> contextUserCodec(Codec<T> unvalidatedCodec, LootContextParamSet params, String contextName)
    {
        return unvalidatedCodec.validate(value ->
        {
            ProblemReporter.Collector reporter = new ProblemReporter.Collector();
            ValidationContext context = new ValidationContext(reporter, params);
            value.validate(context);

            if (reporter.getReport().isEmpty())
                return DataResult.success(value);
            else
                return DataResult.error(() -> String.format("Validation error in %s: %s", contextName, reporter.getReport().get()));
        });
    }

    public static Codec<LootItemCondition> conditionsCodec(LootContextParamSet params, String contextName)
    {
        return contextUserCodec(LootItemCondition.DIRECT_CODEC, params, contextName);
    }

    public static Set<LootContextParam<?>> joinReferencedParams(LootContextUser... users)
    {
        return switch (users.length)
        {
            case 0 -> Set.of();
            case 1 -> users[0].getReferencedContextParams();
            case 2 -> Sets.union(users[0].getReferencedContextParams(), users[1].getReferencedContextParams());
            default -> Stream.of(users).flatMap(o -> o.getReferencedContextParams().stream()).collect(LimaStreamsUtil.toUnmodifiableObjectSet());
        };
    }

    //#region Loot context helper factories
    public static LootContext contextOf(LootParams params)
    {
        return new LootContext.Builder(params).create(Optional.empty());
    }

    public static LootContext emptyLootContext(ServerLevel level)
    {
        LootParams params = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
        return contextOf(params);
    }

    public static LootContext chestLootContext(ServerLevel level, Entity target, @Nullable Entity attacker)
    {
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, target.position())
                .withOptionalParameter(LootContextParams.THIS_ENTITY, target)
                .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, attacker)
                .create(LootContextParamSets.CHEST);

        return contextOf(params);
    }

    public static LootContext entityLootContext(ServerLevel level, Entity thisEntity, Vec3 origin, DamageSource damageSource)
    {
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, origin)
                .withParameter(LootContextParams.DAMAGE_SOURCE, damageSource)
                .withParameter(LootContextParams.THIS_ENTITY, thisEntity)
                .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, damageSource.getEntity())
                .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, damageSource.getDirectEntity())
                .create(LootContextParamSets.ENTITY);

        return contextOf(params);
    }

    public static LootContext entityLootContext(ServerLevel level, Entity thisEntity, DamageSource damageSource)
    {
        return entityLootContext(level, thisEntity, thisEntity.position(), damageSource);
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