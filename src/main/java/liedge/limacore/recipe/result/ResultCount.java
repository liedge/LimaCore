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

public record ResultCount(int min, int max, float chance)
{
    public static final StreamCodec<ByteBuf, ResultCount> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ResultCount::min,
            ByteBufCodecs.VAR_INT, ResultCount::max,
            ByteBufCodecs.FLOAT, ResultCount::chance,
            ResultCount::new);

    public static Codec<ResultCount> codec(int limit)
    {
        Preconditions.checkArgument(limit > 0, "Result count max limit must be greater than 0");

        Codec<Integer> elementCodec = Codec.intRange(0, limit);
        Codec<ResultCount> recordCodec = RecordCodecBuilder.<ResultCount>create(i -> i.group(
                elementCodec.fieldOf("min").forGetter(ResultCount::min),
                elementCodec.fieldOf("max").forGetter(ResultCount::max),
                Codec.floatRange(0f, 1f).optionalFieldOf("chance", 1f).forGetter(ResultCount::chance))
                .apply(i, ResultCount::new)).validate(ResultCount::validate);

        return Codec.either(elementCodec, recordCodec).xmap(either -> either.map(ResultCount::exactly, Function.identity()), ResultCount::encode);
    }

    public static ResultCount of(int min, int max, float chance)
    {
        return new ResultCount(min, max, chance);
    }

    public static ResultCount between(int min, int max)
    {
        return of(min, max, 1);
    }

    public static ResultCount exactlyRandom(int count, float chance)
    {
        return of(count, count, chance);
    }

    public static ResultCount exactly(int count)
    {
        return between(count, count);
    }

    public boolean isConstant()
    {
        return min == max;
    }

    public boolean isRandom()
    {
        return chance < 1f;
    }

    public int rollCount(RandomSource random)
    {
        if (chance >= 1 || random.nextFloat() < chance)
        {
            return isConstant() ? max : random.nextIntBetweenInclusive(min ,max);
        }
        else
        {
            return 0;
        }
    }

    private Either<Integer, ResultCount> encode()
    {
        if (isConstant() && !isRandom())
            return Either.left(min);
        else
            return Either.right(this);
    }

    private DataResult<ResultCount> validate()
    {
        if (max >= min)
            return DataResult.success(this);
        else
            return DataResult.error(() -> String.format("Result minimum amount (%s) is higher than the maximum amount (%s)", min, max));
    }
}