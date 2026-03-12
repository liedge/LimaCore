package liedge.limacore.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.lib.MinMaxRange;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import net.minecraft.advancements.criterion.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record LivingHealthPredicate(MinMaxRange<Float> health) implements EntitySubPredicate
{
    public static final MapCodec<LivingHealthPredicate> CODEC = MinMaxRange.codec(Codec.FLOAT).fieldOf("health").xmap(LivingHealthPredicate::new, LivingHealthPredicate::health);

    public static LivingHealthPredicate checkHealth(MinMaxRange<Float> health)
    {
        return new LivingHealthPredicate(health);
    }

    @Override
    public MapCodec<? extends EntitySubPredicate> codec()
    {
        return LimaCoreLootRegistries.LIVING_HEALTH_PREDICATE.get();
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position)
    {
        return entity instanceof LivingEntity livingEntity && health.test(livingEntity.getHealth());
    }
}