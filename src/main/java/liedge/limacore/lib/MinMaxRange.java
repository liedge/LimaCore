package liedge.limacore.lib;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.util.LimaCoreObjects;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public record MinMaxRange<T extends Comparable<T>>(Optional<T> min, Optional<T> max) implements Predicate<T>
{
    public static <E extends Comparable<E>> Codec<MinMaxRange<E>> codec(Codec<E> elementCodec)
    {
        Codec<MinMaxRange<E>> recordCodec = RecordCodecBuilder.<MinMaxRange<E>>create(instance -> instance.group(
                elementCodec.optionalFieldOf("min").forGetter(MinMaxRange::min),
                elementCodec.optionalFieldOf("max").forGetter(MinMaxRange::max))
                .apply(instance, MinMaxRange::new)).validate(MinMaxRange::validate);

        return Codec.either(elementCodec, recordCodec).xmap(
                either -> either.map(MinMaxRange::exactly, Function.identity()),
                MinMaxRange::encode);
    }

    public static <E extends Comparable<E>> MinMaxRange<E> of(@Nullable E min, @Nullable E max)
    {
        return new MinMaxRange<>(Optional.ofNullable(min), Optional.ofNullable(max));
    }

    public static <E extends Comparable<E>> MinMaxRange<E> any()
    {
        return of(null, null);
    }

    public static <E extends Comparable<E>> MinMaxRange<E> atLeast(E min)
    {
        return of(min, null);
    }

    public static <E extends Comparable<E>> MinMaxRange<E> atMost(E max)
    {
        return of(null, max);
    }

    public static <E extends Comparable<E>> MinMaxRange<E> exactly(E element)
    {
        return of(element, element);
    }

    // Class def
    private DataResult<MinMaxRange<T>> validate()
    {
        if (min.isPresent() && max.isPresent())
        {
            T lower = min.get();
            T upper = max.get();

            if (LimaCoreObjects.greaterThan(lower, upper)) return DataResult.error(() -> String.format("Min[%s] cannot be greater than max[%s]", min, max));
        }

        return DataResult.success(this);
    }

    private Either<T, MinMaxRange<T>> encode()
    {
        if (min.equals(max) && min.isPresent())
        {
            return Either.left(min.get());
        }
        else
        {
            return Either.right(this);
        }
    }

    public boolean testMin(T value)
    {
        return LimaCoreObjects.greaterThanOrEquals(value, min.orElse(null));
    }

    public boolean testMax(T value)
    {
        return LimaCoreObjects.lessThanOrEquals(value, max.orElse(null));
    }

    @Override
    public boolean test(T value)
    {
        return testMin(value) && testMax(value);
    }

    public T clampMin(T value)
    {
        T lower = min.orElse(null);
        return LimaCoreObjects.greaterThanOrEquals(value, lower) ? value : lower;
    }

    public T clampMax(T value)
    {
        T upper = max.orElse(null);
        return LimaCoreObjects.lessThanOrEquals(value, upper) ? value : upper;
    }

    public T clamp(T value)
    {
        return clampMax(clampMin(value));
    }
}