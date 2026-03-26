package liedge.limacore.world.loot.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.util.LimaLootUtil;
import liedge.limacore.world.loot.position.ContextPosition;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public record LootContextDistance(ContextPosition start, ContextPosition end, NumberProvider fallback) implements NumberProvider
{
    public static final MapCodec<LootContextDistance> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ContextPosition.CODEC.fieldOf("start").forGetter(LootContextDistance::start),
            ContextPosition.CODEC.fieldOf("end").forGetter(LootContextDistance::end),
            NumberProviders.CODEC.optionalFieldOf("fallback", ConstantValue.exactly(0)).forGetter(LootContextDistance::fallback))
            .apply(instance, LootContextDistance::new));

    public static LootContextDistance between(ContextPosition start, ContextPosition end, NumberProvider fallback)
    {
        return new LootContextDistance(start, end, fallback);
    }

    public static LootContextDistance between(ContextPosition start, ContextPosition end)
    {
        return between(start, end, ConstantValue.exactly(0));
    }

    @Override
    public float getFloat(LootContext context)
    {
        Vec3 startPos = start.get(context);
        Vec3 endPos = end.get(context);

        return startPos != null && endPos != null ? (float) startPos.distanceTo(endPos) : fallback.getFloat(context);
    }

    @Override
    public MapCodec<? extends NumberProvider> codec()
    {
        return CODEC;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams()
    {
        return LimaLootUtil.joinReferencedParams(start, end, fallback);
    }
}