package liedge.limacore.world.loot.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.MinMaxRange;
import liedge.limacore.lib.MobHostility;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import liedge.limacore.util.LimaCoreObjects;
import liedge.limacore.util.LimaEntityUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public final class EntityHostilityLootCondition extends EntityComparisonLootCondition
{
    public static final MapCodec<EntityHostilityLootCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
            .and(MobHostility.BOUNDS_CODEC.fieldOf("bounds").forGetter(o -> o.bounds))
            .apply(instance, EntityHostilityLootCondition::new));

    public static LootItemCondition.Builder create(LootContext.EntityTarget attacker, LootContext.EntityTarget targeted, MinMaxRange<MobHostility> bounds)
    {
        return () -> new EntityHostilityLootCondition(attacker, targeted, bounds);
    }

    public static LootItemCondition.Builder create(MinMaxRange<MobHostility> bounds)
    {
        return create(LootContext.EntityTarget.THIS, LootContext.EntityTarget.ATTACKER, bounds);
    }

    private final MinMaxRange<MobHostility> bounds;

    public EntityHostilityLootCondition(LootContext.EntityTarget attacker, LootContext.EntityTarget targeted, MinMaxRange<MobHostility> bounds)
    {
        super(attacker, targeted);
        this.bounds = bounds;
    }

    @Override
    protected boolean testEntities(Entity firstEntity, Entity secondEntity)
    {
        LivingEntity livingSecond = LimaCoreObjects.tryCast(LivingEntity.class, secondEntity);
        return bounds.test(LimaEntityUtil.getEntityHostility(firstEntity, livingSecond));
    }

    @Override
    public LootItemConditionType getType()
    {
        return LimaCoreLootRegistries.HOSTILITY_CONDITION.get();
    }
}