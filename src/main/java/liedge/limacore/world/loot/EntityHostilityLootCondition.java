package liedge.limacore.world.loot;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.MobHostility;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limacore.util.LimaEntityUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public final class EntityHostilityLootCondition extends EntityComparisonLootCondition
{
    public static final MapCodec<EntityHostilityLootCondition> CODEC = RecordCodecBuilder.<EntityHostilityLootCondition>mapCodec(instance -> commonFields(instance)
            .and(MobHostility.CODEC.optionalFieldOf("min", MobHostility.PASSIVE).forGetter(o -> o.min))
            .and(MobHostility.CODEC.optionalFieldOf("max", MobHostility.HOSTILE).forGetter(o -> o.max))
            .apply(instance, EntityHostilityLootCondition::new)).validate(EntityHostilityLootCondition::validate);

    private static DataResult<EntityHostilityLootCondition> validate(EntityHostilityLootCondition value)
    {
        if (value.max.atLeast(value.min))
        {
            return DataResult.success(value);
        }
        else
        {
            return DataResult.error(() -> "Minimum hostility is higher than maximum.");
        }
    }

    public static LootItemCondition.Builder between(MobHostility min, MobHostility max)
    {
        return () -> new EntityHostilityLootCondition(LootContext.EntityTarget.THIS, LootContext.EntityTarget.ATTACKER, min, max);
    }

    public static LootItemCondition.Builder atLeast(MobHostility min)
    {
        return between(min, MobHostility.HOSTILE);
    }

    public static LootItemCondition.Builder atMost(MobHostility max)
    {
        return between(MobHostility.PASSIVE, max);
    }

    private final MobHostility min;
    private final MobHostility max;

    public EntityHostilityLootCondition(LootContext.EntityTarget attacker, LootContext.EntityTarget targeted, MobHostility min, MobHostility max)
    {
        super(attacker, targeted);
        this.min = min;
        this.max = max;
    }

    @Override
    protected boolean testEntities(Entity firstEntity, Entity secondEntity)
    {
        LivingEntity livingSecond = LimaCoreUtil.castOrNull(LivingEntity.class, secondEntity);
        return LimaEntityUtil.getEntityHostility(firstEntity, livingSecond).between(min, max);
    }

    @Override
    public LootItemConditionType getType()
    {
        return LimaCoreLootRegistries.HOSTILITY_CONDITION.get();
    }
}