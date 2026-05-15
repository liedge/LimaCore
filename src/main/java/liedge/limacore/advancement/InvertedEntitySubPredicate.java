package liedge.limacore.advancement;

import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.criterion.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public record InvertedEntitySubPredicate(EntitySubPredicate child) implements EntitySubPredicate
{
    public static final MapCodec<InvertedEntitySubPredicate> CODEC = EntitySubPredicate.CODEC.fieldOf("child").xmap(InvertedEntitySubPredicate::new, InvertedEntitySubPredicate::child);

    public static InvertedEntitySubPredicate of(EntitySubPredicate child)
    {
        return new  InvertedEntitySubPredicate(child);
    }

    @Override
    public MapCodec<? extends EntitySubPredicate> codec()
    {
        return CODEC;
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position)
    {
        return !child.matches(entity, level, position);
    }
}