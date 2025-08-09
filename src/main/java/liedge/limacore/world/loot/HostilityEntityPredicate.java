package liedge.limacore.world.loot;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.MobHostility;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import liedge.limacore.util.LimaEntityUtil;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record HostilityEntityPredicate(MobHostility min, MobHostility max) implements EntitySubPredicate
{
    public static final MapCodec<HostilityEntityPredicate> CODEC = RecordCodecBuilder.<HostilityEntityPredicate>mapCodec(instance -> instance.group(
            MobHostility.CODEC.optionalFieldOf("min", MobHostility.PASSIVE).forGetter(HostilityEntityPredicate::min),
            MobHostility.CODEC.optionalFieldOf("max", MobHostility.HOSTILE).forGetter(HostilityEntityPredicate::max))
            .apply(instance, HostilityEntityPredicate::new)).validate(HostilityEntityPredicate::validate);

    private static DataResult<HostilityEntityPredicate> validate(HostilityEntityPredicate value)
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

    public static HostilityEntityPredicate between(MobHostility min, MobHostility max)
    {
        return new HostilityEntityPredicate(min, max);
    }

    public static HostilityEntityPredicate atLeast(MobHostility min)
    {
        return between(min, MobHostility.HOSTILE);
    }

    public static HostilityEntityPredicate atMost(MobHostility max)
    {
        return between(MobHostility.PASSIVE, max);
    }

    @Override
    public MapCodec<? extends EntitySubPredicate> codec()
    {
        return LimaCoreLootRegistries.HOSTILITY_ENTITY_PREDICATE.get();
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position)
    {
        MobHostility hostility = LimaEntityUtil.getEntityHostility(entity, null);
        return hostility.between(min, max);
    }
}