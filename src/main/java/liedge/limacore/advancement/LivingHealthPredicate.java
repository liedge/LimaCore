package liedge.limacore.advancement;

import com.mojang.serialization.MapCodec;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record LivingHealthPredicate(MinMaxBounds.Doubles health) implements EntitySubPredicate
{
    public static final MapCodec<LivingHealthPredicate> CODEC = MinMaxBounds.Doubles.CODEC.xmap(LivingHealthPredicate::new, LivingHealthPredicate::health).fieldOf("health");

    @Override
    public MapCodec<? extends EntitySubPredicate> codec()
    {
        return LimaCoreLootRegistries.LIVING_HEALTH_PREDICATE.get();
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position)
    {
        return entity instanceof LivingEntity livingEntity && health.matches(livingEntity.getHealth());
    }
}