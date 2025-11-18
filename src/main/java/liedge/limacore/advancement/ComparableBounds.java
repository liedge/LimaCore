package liedge.limacore.advancement;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public record ComparableBounds<T extends Comparable<T>>(Optional<T> min, Optional<T> max) implements Predicate<T>
{
    private static <E extends Comparable<E>> DataResult<ComparableBounds<E>> validate(ComparableBounds<E> bounds)
    {
        if (bounds.min.isPresent() && bounds.max.isPresent() && bounds.min.get().compareTo(bounds.max.get()) > 0)
            return DataResult.error(() -> String.format("Comparable min[%s] cannot be greater than max[%s].", bounds.min, bounds.max));
        else
            return DataResult.success(bounds);
    }

    public static <E extends Comparable<E>> Codec<ComparableBounds<E>> codec(Codec<E> elementCodec)
    {
        Codec<ComparableBounds<E>> recordCodec = RecordCodecBuilder.<ComparableBounds<E>>create(instance -> instance.group(
                elementCodec.optionalFieldOf("min").forGetter(ComparableBounds::min),
                elementCodec.optionalFieldOf("max").forGetter(ComparableBounds::max))
                .apply(instance, ComparableBounds::new)).validate(ComparableBounds::validate);

        return Codec.either(elementCodec, recordCodec).xmap(
                either -> either.map(ComparableBounds::exactly, Function.identity()),
                bounds -> {
                    if (bounds.min.equals(bounds.max) && bounds.min.isPresent())
                        return Either.left(bounds.min.get());
                    else
                        return Either.right(bounds);
                });
    }

    public static <E extends Comparable<E>> ComparableBounds<E> of(@Nullable E min, @Nullable E max)
    {
        return new ComparableBounds<>(Optional.ofNullable(min), Optional.ofNullable(max));
    }

    public static <E extends Comparable<E>> ComparableBounds<E> any()
    {
        return of(null, null);
    }

    public static <E extends Comparable<E>> ComparableBounds<E> atLeast(E min)
    {
        return of(min, null);
    }

    public static <E extends Comparable<E>> ComparableBounds<E> atMost(E max)
    {
        return of(null, max);
    }

    public static <E extends Comparable<E>> ComparableBounds<E> exactly(E element)
    {
        return of(element, element);
    }

    @Override
    public boolean test(T value)
    {
        boolean lower = min().map(o -> value.compareTo(o) >= 0).orElse(true);
        boolean upper = max().map(o -> value.compareTo(o) <= 0).orElse(true);

        return lower && upper;
    }
}