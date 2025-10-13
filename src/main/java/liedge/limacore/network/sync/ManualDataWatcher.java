package liedge.limacore.network.sync;

import liedge.limacore.network.NetworkSerializer;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ManualDataWatcher<T> extends LimaDataWatcher<T>
{
    public static <T> LimaDataWatcher<T> manuallyTrack(NetworkSerializer<T> serializer, Supplier<T> getter, Consumer<T> setter)
    {
        return new ManualDataWatcher<>(serializer, getter, setter);
    }

    public static <T> LimaDataWatcher<T> manuallyTrack(Supplier<? extends NetworkSerializer<T>> supplier, Supplier<T> getter, Consumer<T> setter)
    {
        return new ManualDataWatcher<>(supplier.get(), getter, setter);
    }

    private final Supplier<T> getter;
    private final Consumer<T> setter;

    private ManualDataWatcher(NetworkSerializer<T> serializer, Supplier<T> getter, Consumer<T> setter)
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