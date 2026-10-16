package liedge.limacore.advancement;

import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Targeting;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public record TargetingSubPredicate(Optional<EntityPredicate> predicate) implements EntitySubPredicate
{
    public static final MapCodec<TargetingSubPredicate> CODEC = EntityPredicate.CODEC.optionalFieldOf("target").xmap(TargetingSubPredicate::new, TargetingSubPredicate::predicate);

    @Override
    public MapCodec<? extends EntitySubPredicate> codec()
    {
        return CODEC;
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position)
    {
        if (entity instanceof Targeting targeting)
        {
            LivingEntity target = targeting.getTarget();
            return target != null && predicate.map(p -> p.matches(level, target.position(), target)).orElse(true);
        }

        return false;
    }
}