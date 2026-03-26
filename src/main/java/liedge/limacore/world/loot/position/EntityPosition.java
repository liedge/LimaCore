package liedge.limacore.world.loot.position;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public record EntityPosition(LootContext.EntityTarget target) implements ContextPosition
{
    static final Codec<EntityPosition> INLINE_CODEC = LootContext.EntityTarget.CODEC.xmap(EntityPosition::new, EntityPosition::target);
    static final MapCodec<EntityPosition> CODEC = INLINE_CODEC.fieldOf("entity_target");

    @Override
    public @Nullable Vec3 get(LootContext context)
    {
        Entity entity = context.getOptionalParameter(target.contextParam());
        return entity != null ? entity.position() : null;
    }

    @Override
    public Type getType()
    {
        return Type.CONTEXT_ENTITY;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams()
    {
        return Set.of(target.contextParam());
    }
}