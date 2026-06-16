package liedge.limacore.network.sync;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public final class NullableValueTracker<T> extends ValueTracker<Optional<T>>
{
    public static <T> ValueTracker<Optional<T>> create(NetworkSerializer<Optional<T>> serializer, OptionalGetter<T> getter, OptionalSetter<T> setter)
    {
        return new NullableValueTracker<>(serializer, getter, setter);
    }

    public static <T> ValueTracker<Optional<T>> create(Supplier<? extends NetworkSerializer<Optional<T>>> serializer, OptionalGetter<T> getter, OptionalSetter<T> setter)
    {
        return new NullableValueTracker<>(serializer.get(), getter, setter);
    }

    public static ValueTracker<Integer> createClientEntity(OptionalGetter<Entity> getter, OptionalSetter<Entity> setter)
    {
        return SimpleValueTracker.create(LimaCoreNetworkSerializers.VAR_INT, () -> {
            Entity entity = getter.get();
            return entity != null && !entity.isRemoved() ? entity.getId() : -1;
        }, eid -> setter.set(LimaCoreClientUtil.getClientEntity(eid)));
    }

    private final OptionalGetter<T> getter;
    private final OptionalSetter<T> setter;

    private NullableValueTracker(NetworkSerializer<Optional<T>> serializer, OptionalGetter<T> getter, OptionalSetter<T> setter)
    {
        super(serializer);
        this.getter = getter;
        this.setter = setter;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Optional<T> getCurrentData()
    {
        return Optional.ofNullable(getter.get());
    }

    @Override
    protected void setCurrentData(Optional<T> currentData)
    {
        setter.set(currentData.orElse(null));
    }

    @FunctionalInterface
    public interface OptionalGetter<T>
    {
        @Nullable T get();
    }

    @FunctionalInterface
    public interface OptionalSetter<T>
    {
        void set(@Nullable T value);
    }
}