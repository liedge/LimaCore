package liedge.limacore.world.loot;

import com.mojang.serialization.MapCodec;
import liedge.limacore.util.LimaCoreUtil;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class HostileEntitySubPredicate implements EntitySubPredicate
{
    public static final HostileEntitySubPredicate INSTANCE = new HostileEntitySubPredicate();
    public static final MapCodec<HostileEntitySubPredicate> PREDICATE_CODEC = MapCodec.unit(INSTANCE);

    private HostileEntitySubPredicate() {}

    @Override
    public MapCodec<? extends EntitySubPredicate> codec()
    {
        return PREDICATE_CODEC;
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position)
    {
        return LimaCoreUtil.isEntityHostile(entity);
    }
}