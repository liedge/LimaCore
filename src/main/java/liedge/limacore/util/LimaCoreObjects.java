package liedge.limacore.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class LimaCoreObjects
{
    private LimaCoreObjects() {}

    public static @Nullable <T> T tryCast(Class<T> type, @Nullable Object o)
    {
        return type.isInstance(o) ? type.cast(o) : null;
    }

    public static <T, X extends Throwable> T cast(Class<T> type, @Nullable Object o, Supplier<X> errSupplier) throws X
    {
        T val = tryCast(type, o);
        if (val != null)
            return val;
        else
            throw errSupplier.get();
    }

    public static <T> T cast(Class<T> type, @Nullable Object o, String errMessage)
    {
        return cast(type, o, () -> new ClassCastException(errMessage));
    }

    public static <T> T cast(Class<T> type, @Nullable Object o)
    {
        return cast(type, o, () -> new ClassCastException("Object is not an instance of " + type.getSimpleName()));
    }

    //#region Comparison helpers
    @Contract("_,null->true")
    public static <T extends Comparable<T>> boolean greaterThan(T value, @Nullable T bound)
    {
        return bound == null || value.compareTo(bound) > 0;
    }

    @Contract("_,null->true")
    public static <T extends Comparable<T>> boolean greaterThanOrEquals(T value, @Nullable T bound)
    {
        return bound == null || value.compareTo(bound) >= 0;
    }

    @Contract("_,null->true")
    public static <T extends Comparable<T>> boolean lessThan(T value, @Nullable T bound)
    {
        return bound == null || value.compareTo(bound) < 0;
    }

    @Contract("_,null->true")
    public static <T extends Comparable<T>> boolean lessThanOrEquals(T value, @Nullable T bound)
    {
        return bound == null || value.compareTo(bound) <= 0;
    }
    //#endregion
}