package liedge.limacore.recipe.result;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;

import java.util.function.Function;
import java.util.function.ToIntFunction;

public record ResultCount(int min, int max) implements ToIntFunction<RandomSource>
{
    private static DataResult<ResultCount> validate(ResultCount value)
    {
        if (value.max > value.min)
            return DataResult.success(value);
        else
            return DataResult.error(() -> String.format("Item result max count (%s) must be greater than min count (%s)", value.max, value.min));
    }

    public static Codec<ResultCount> codec(int limit)
    {
        Preconditions.checkArgument(limit > 0, "Result count codec limit must be at least 1.");
        Codec<Integer> elementCodec = Codec.intRange(0, limit);
        Codec<ResultCount> recordCodec = RecordCodecBuilder.<ResultCount>create(instance -> instance.group(
                elementCodec.fieldOf("min").forGetter(ResultCount::min),
                elementCodec.fieldOf("max").forGetter(ResultCount::max))
                .apply(instance, ResultCount::new)).validate(ResultCount::validate);

        return Codec.either(elementCodec, recordCodec).xmap(
                either -> either.map(ResultCount::exactly, Function.identity()),
                count -> count.isConstant() ? Either.left(count.min) : Either.right(count));
    }

    public static final StreamCodec<ByteBuf, ResultCount> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ResultCount::min,
            ByteBufCodecs.VAR_INT, ResultCount::max,
            ResultCount::new);

    public static ResultCount exactly(int count)
    {
        return new ResultCount(count, count);
    }

    public static ResultCount between(int min, int max)
    {
        return new ResultCount(min, max);
    }

    public boolean isConstant()
    {
        return min == max;
    }

    @Override
    public int applyAsInt(RandomSource random)
    {
        return isConstant() ? min : random.nextIntBetweenInclusive(min, max);
    }
}