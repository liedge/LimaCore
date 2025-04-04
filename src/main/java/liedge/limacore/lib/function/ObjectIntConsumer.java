package liedge.limacore.lib.function;

import java.util.function.BiConsumer;

/**
 * Represents a function that accepts an object and an integer, and produces no result.
 * Avoids the overhead of auto-boxing/auto-unboxing the integer parameter.
 * @param <P1> The type of the first argument
 */
public interface ObjectIntConsumer<P1> extends BiConsumer<P1, Integer>
{
    void acceptWithInt(P1 p1, int p2);

    @Deprecated
    @Override
    default void accept(P1 p1, Integer p2)
    {
        acceptWithInt(p1, p2);
    }
}