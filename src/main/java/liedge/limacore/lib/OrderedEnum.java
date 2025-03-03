package liedge.limacore.lib;

import liedge.limacore.util.LimaCollectionsUtil;

import java.util.Set;

public interface OrderedEnum<E extends Enum<E>>
{
    static <T extends Enum<T> & OrderedEnum<T>> T nextAvailable(Set<T> validValues, T current)
    {
        if (validValues.isEmpty()) return current;
        if (validValues.size() == 1) return validValues.iterator().next();

        T next = null;

        while (next == null)
        {
            current = current.next();
            if (validValues.contains(current)) next = current;
        }

        return next;
    }

    static <T extends Enum<T> & OrderedEnum<T>> T previousAvailable(Set<T> validValues, T current)
    {
        if (validValues.isEmpty()) return current;
        if (validValues.size() == 1) return validValues.iterator().next();

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
        return LimaCollectionsUtil.checkedEnumConstants(getDeclaringClass());
    }
}