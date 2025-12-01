package liedge.limacore.data;

import com.google.common.base.Suppliers;
import com.mojang.serialization.*;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class LazyMapCodec<A> extends MapCodec<A>
{
    public static <T> MapCodec<T> of(String name, Supplier<MapCodec<T>> supplier)
    {
        return new LazyMapCodec<>(supplier, "LazyMapCodec[" + name + "]");
    }

    public static <T> MapCodec<T> of(Supplier<MapCodec<T>> supplier)
    {
        return of(supplier.toString(), supplier);
    }

    private final Supplier<MapCodec<A>> wrapped;
    private final String name;

    private LazyMapCodec(Supplier<MapCodec<A>> wrapped, String name)
    {
        this.wrapped = Suppliers.memoize(wrapped::get);
        this.name = name;
    }

    @Override
    public <T> Stream<T> keys(DynamicOps<T> ops)
    {
        return wrapped.get().keys(ops);
    }

    @Override
    public <T> DataResult<A> decode(DynamicOps<T> ops, MapLike<T> input)
    {
        return wrapped.get().decode(ops, input);
    }

    @Override
    public <T> RecordBuilder<T> encode(A input, DynamicOps<T> ops, RecordBuilder<T> prefix)
    {
        return wrapped.get().encode(input, ops, prefix);
    }

    @Override
    public String toString()
    {
        return name;
    }
}