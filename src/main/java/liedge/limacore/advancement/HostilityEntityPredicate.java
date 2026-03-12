package liedge.limacore.advancement;

import com.mojang.serialization.MapCodec;
import liedge.limacore.lib.MinMaxRange;
import liedge.limacore.lib.MobHostility;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import liedge.limacore.util.LimaEntityUtil;
import net.minecraft.advancements.criterion.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record HostilityEntityPredicate(MinMaxRange<MobHostility> bounds) implements EntitySubPredicate
{
    public static final MapCodec<HostilityEntityPredicate> CODEC = MobHostility.BOUNDS_CODEC.xmap(HostilityEntityPredicate::new, HostilityEntityPredicate::bounds).fieldOf("bounds");

    public static HostilityEntityPredicate of(MinMaxRange<MobHostility> bounds)
    {
        return new HostilityEntityPredicate(bounds);
    }

    @Override
    public MapCodec<? extends EntitySubPredicate> codec()
    {
        return LimaCoreLootRegistries.HOSTILITY_ENTITY_PREDICATE.get();
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position)
    {
        MobHostility hostility = LimaEntityUtil.getEntityHostility(level, entity, null);
        return bounds.test(hostility);
    }
}