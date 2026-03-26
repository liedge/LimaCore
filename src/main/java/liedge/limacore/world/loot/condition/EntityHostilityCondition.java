package liedge.limacore.world.loot.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.MinMaxRange;
import liedge.limacore.lib.MobHostility;
import liedge.limacore.util.LimaCoreObjects;
import liedge.limacore.util.LimaEntityUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Set;

public record EntityHostilityCondition(LootContext.EntityTarget victim, LootContext.EntityTarget attacker, MinMaxRange<MobHostility> bounds) implements LootItemCondition
{
    public static final MapCodec<EntityHostilityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LootContext.EntityTarget.CODEC.optionalFieldOf("victim", LootContext.EntityTarget.THIS).forGetter(EntityHostilityCondition::victim),
            LootContext.EntityTarget.CODEC.optionalFieldOf("attacker", LootContext.EntityTarget.ATTACKER).forGetter(EntityHostilityCondition::attacker),
            MobHostility.BOUNDS_CODEC.fieldOf("hostility").forGetter(EntityHostilityCondition::bounds))
            .apply(instance, EntityHostilityCondition::new));

    public static LootItemCondition.Builder of(LootContext.EntityTarget victim, LootContext.EntityTarget attacker, MinMaxRange<MobHostility> bounds)
    {
        return () -> new EntityHostilityCondition(victim, attacker, bounds);
    }

    public static LootItemCondition.Builder attackerIs(MinMaxRange<MobHostility> bounds)
    {
        return of(LootContext.EntityTarget.THIS, LootContext.EntityTarget.ATTACKER, bounds);
    }

    @Override
    public MapCodec<? extends LootItemCondition> codec()
    {
        return CODEC;
    }

    @Override
    public boolean test(LootContext context)
    {
        Entity victim = context.getOptionalParameter(this.victim.contextParam());
        LivingEntity attacker = LimaCoreObjects.tryCast(LivingEntity.class, context.getOptionalParameter(this.attacker.contextParam()));

        if (victim != null && attacker != null && victim.level() instanceof ServerLevel level)
        {
            MobHostility hostility = LimaEntityUtil.getEntityHostility(level, victim, attacker);
            return bounds.test(hostility);
        }

        return false;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams()
    {
        return Set.of(attacker.contextParam(), victim.contextParam());
    }
}