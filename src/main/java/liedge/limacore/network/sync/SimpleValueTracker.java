package liedge.limacore.network.sync;

import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaCollectionsUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class SimpleValueTracker<T> extends ValueTracker<T>
{
    public static <T> ValueTracker<T> create(NetworkSerializer<T> serializer, Supplier<T> getter, Consumer<T> setter)
    {
        return new SimpleValueTracker<>(serializer, getter, setter);
    }

    public static <T> ValueTracker<T> create(Supplier<? extends NetworkSerializer<T>> serializer, Supplier<T> getter, Consumer<T> setter)
    {
        return new SimpleValueTracker<>(serializer.get(), getter, setter);
    }

    public static <E extends Enum<E>> ValueTracker<Integer> createEnum(Class<E> enumClass, Supplier<E> getter, Consumer<E> setter)
    {
        return create(LimaCoreNetworkSerializers.VAR_INT,
                () -> getter.get().ordinal(),
                ordinal -> setter.accept(LimaCollectionsUtil.getEnumByOrdinal(enumClass, ordinal)));
    }

    private final Supplier<T> getter;
    private final Consumer<T> setter;

    private SimpleValueTracker(NetworkSerializer<T> serializer, Supplier<T> getter, Consumer<T> setter)
    {
        super(serializer);
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    protected T getCurrentData()
    {
        return getter.get();
    }

    @Override
    protected void setCurrentData(T currentData)
    {
        setter.accept(currentData);
    }
}