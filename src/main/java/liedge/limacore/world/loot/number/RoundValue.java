package liedge.limacore.world.loot.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.lib.math.LimaRoundingMode;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.Set;

public record RoundValue(NumberProvider child, LimaRoundingMode mode) implements NumberProvider
{
    public static final MapCodec<RoundValue> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviders.CODEC.fieldOf("child").forGetter(RoundValue::child),
            LimaRoundingMode.CODEC.optionalFieldOf("mode", LimaRoundingMode.NATURAL).forGetter(RoundValue::mode))
            .apply(instance, RoundValue::new));

    public static RoundValue create(NumberProvider child, LimaRoundingMode mode)
    {
        return new RoundValue(child, mode);
    }

    public static RoundValue roundValue(NumberProvider child)
    {
        return create(child, LimaRoundingMode.NATURAL);
    }

    public static RoundValue ceil(NumberProvider child)
    {
        return create(child, LimaRoundingMode.CEIL);
    }

    public static RoundValue floor(NumberProvider child)
    {
        return create(child, LimaRoundingMode.FLOOR);
    }

    public static RoundValue roundRandomly(NumberProvider child)
    {
        return create(child, LimaRoundingMode.RANDOM);
    }

    @Override
    public float getFloat(LootContext context)
    {
        return getInt(context);
    }

    @Override
    public int getInt(LootContext context)
    {
        return LimaCoreMath.round(child.getFloat(context), mode);
    }

    @Override
    public MapCodec<? extends NumberProvider> codec()
    {
        return CODEC;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams()
    {
        return child.getReferencedContextParams();
    }
}