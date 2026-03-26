package liedge.limacore.world.loot.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.MinMaxRange;
import liedge.limacore.util.LimaLootUtil;
import liedge.limacore.world.loot.position.ContextPosition;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public record DistanceCheckCondition(ContextPosition start, ContextPosition end, MinMaxRange<Double> distance) implements LootItemCondition
{
    public static final MapCodec<DistanceCheckCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ContextPosition.CODEC.fieldOf("start").forGetter(DistanceCheckCondition::start),
            ContextPosition.CODEC.fieldOf("end").forGetter(DistanceCheckCondition::end),
            MinMaxRange.codec(Codec.doubleRange(0d, 4096d)).fieldOf("distance").forGetter(DistanceCheckCondition::distance))
            .apply(instance, DistanceCheckCondition::new));

    public static LootItemCondition.Builder checkDistance(ContextPosition start, ContextPosition end, MinMaxRange<Double> distance)
    {
        return () -> new DistanceCheckCondition(start, end, distance);
    }

    @Override
    public MapCodec<? extends LootItemCondition> codec()
    {
        return CODEC;
    }

    @Override
    public boolean test(LootContext context)
    {
        Vec3 a = start.get(context);
        Vec3 b = end.get(context);

        return a != null && b != null && distance.test(a.distanceTo(b));
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams()
    {
        return LimaLootUtil.joinReferencedParams(start, end);
    }
}