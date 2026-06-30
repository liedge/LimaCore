package liedge.limacore.util;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import liedge.limacore.advancement.EnchantmentLevelEntityPredicate;
import liedge.limacore.advancement.LimaAdvancementUtil;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public final class LimaLootUtil
{
    private LimaLootUtil() {}

    public static <T extends Validatable> Codec<T> validatedCodec(Codec<T> codec, ContextKeySet params)
    {
        return codec.validate(Validatable.validatorForContext(params));
    }

    public static <T extends Validatable> Codec<List<T>> validatedListCodec(Codec<List<T>> codec, ContextKeySet params)
    {
        return codec.validate(Validatable.listValidatorForContext(params));
    }

    public static Codec<LootItemCondition> conditionCodec(ContextKeySet params)
    {
        return validatedCodec(LootItemCondition.DIRECT_CODEC, params);
    }

    public static Codec<List<LootItemCondition>> conditionsCodec(ContextKeySet params)
    {
        return validatedListCodec(LootItemCondition.DIRECT_CODEC.listOf(), params);
    }

    public static Set<ContextKey<?>> joinReferencedParams(LootContextUser... users)
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
        return LootTableIdCondition.builder(lootTableKey.identifier());
    }

    public static LootItemCondition.Builder blockLootTable(Block block)
    {
        return specificLootTable(block.getLootTable().orElseThrow());
    }

    public static LootItemCondition.Builder blockLootTable(Holder<Block> holder)
    {
        return blockLootTable(holder.value());
    }

    public static LootItemCondition.Builder defaultEntityLootTable(EntityType<?> type)
    {
        return specificLootTable(type.getDefaultLootTable().orElseThrow());
    }

    public static LootItemCondition.Builder defaultEntityLootTable(Holder<EntityType<?>> holder)
    {
        return defaultEntityLootTable(holder.value());
    }

    public static LootItemCondition.Builder needsEntityType(EntityType<?> type)
    {
        return LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, LimaAdvancementUtil.matchesEntityType(type));
    }

    public static LootItemCondition.Builder needsEntityTag(HolderGetter<EntityType<?>> holders, TagKey<EntityType<?>> tagKey)
    {
        return LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().of(holders, tagKey));
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

    public static LootItemCondition.Builder matchBlockProperty(Block block, UnaryOperator<StatePropertiesPredicate.Builder> op)
    {
        return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(op.apply(StatePropertiesPredicate.Builder.properties()));
    }

    public static LootItemCondition.Builder matchBlockProperty(Holder<Block> holder, UnaryOperator<StatePropertiesPredicate.Builder> op)
    {
        return matchBlockProperty(holder.value(), op);
    }

    public static <T extends Comparable<T> & StringRepresentable> LootItemCondition.Builder matchBlockProperty(Block block, Property<T> property, T value)
    {
        return matchBlockProperty(block, builder -> builder.hasProperty(property, value));
    }

    public static <T extends Comparable<T> & StringRepresentable> LootItemCondition.Builder matchBlockProperty(Holder<Block> holder, Property<T> property, T value)
    {
        return matchBlockProperty(holder.value(), property, value);
    }

    public static LootItemCondition.Builder matchBlockProperty(Block block, Property<Integer> property, int value)
    {
        return matchBlockProperty(block, builder -> builder.hasProperty(property, value));
    }

    public static LootItemCondition.Builder matchBlockProperty(Holder<Block> holder, Property<Integer> property, int value)
    {
        return matchBlockProperty(holder.value(), property, value);
    }

    public static LootItemCondition.Builder matchBlockProperty(Block block, Property<Boolean> property, boolean value)
    {
        return matchBlockProperty(block, builder -> builder.hasProperty(property, value));
    }

    public static LootItemCondition.Builder matchBlockProperty(Holder<Block> holder, Property<Boolean> property, boolean value)
    {
        return matchBlockProperty(holder.value(), property, value);
    }
}