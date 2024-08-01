package liedge.limacore.lib;

public interface OrderedEnum<E extends Enum<E>>
{
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