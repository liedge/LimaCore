package liedge.limacore.lib;

import java.util.Collection;

public interface OrderedEnum<E extends Enum<E>>
{
    static <T extends Enum<T> & OrderedEnum<T>> T nextAvailable(Collection<T> validValues, T current)
    {
        if (validValues.isEmpty()) return current;

        T next = null;

        while (next == null)
        {
            current = current.next();
            if (validValues.contains(current)) next = current;
        }

        return next;
    }

    static <T extends Enum<T> & OrderedEnum<T>> T previousAvailable(Collection<T> validValues, T current)
    {
        if (validValues.isEmpty()) return current;

        T next = null;

        while (next == null)
        {
            current = current.previous();
            if (validValues.contains(current)) next = current;
        }

        return next;
    }

    default E previous()
    {
        int len = count();
        int i = (ordinal() - 1 + len) % len;
        return enumValues()[i];
    }

    default E next()
    {
        int len = count();
        int i = (ordinal() + 1) % len;
        return enumValues()[i];
    }

    int ordinal();

    Class<E> getDeclaringClass();

    private int count()
    {
        return enumValues().length;
    }

    private E[] enumValues()
    {
        return getDeclaringClass().getEnumConstants();
    }
}