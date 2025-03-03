package liedge.limacore.lib.function;

import java.util.function.BiFunction;

/**
 * A functional interface that represents a function that accepts an object and an {@code int} primitive
 * and produces a result. This interface avoids the overhead of autoboxing by providing a method
 * that operates directly on the primitive type.
 *
 * @param <P1> The type of the first argument.
 * @param <R>  The type of the result.
 */
@FunctionalInterface
public interface ObjectIntFunction<P1, R> extends BiFunction<P1, Integer, R>
{
    R applyWithInt(P1 p1, int p2);

    @Deprecated
    @Override
    default R apply(P1 p1, Integer p2)
    {
        return applyWithInt(p1, p2);
    }
}