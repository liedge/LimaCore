package liedge.limacore.network.sync;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaCollectionsUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public sealed abstract class AutomaticDataWatcher<T> extends LimaDataWatcher<T>
{
    public static <T> LimaDataWatcher<T> keepSynced(NetworkSerializer<T> serializer, Supplier<T> getter, Consumer<T> setter)
    {
        return new SimpleValue<>(serializer, getter, setter);
    }

    public static <T> LimaDataWatcher<T> keepSynced(Supplier<? extends NetworkSerializer<T>> typeSupplier, Supplier<T> getter, Consumer<T> setter)
    {
        return keepSynced(typeSupplier.get(), getter, setter);
    }

    public static <T> LimaDataWatcher<Optional<T>> keepNullableSynced(NetworkSerializer<Optional<T>> serializer, Supplier<@Nullable T> getter, Consumer<@Nullable T> setter)
    {
        return new NullableValue<>(serializer, getter, setter);
    }

    public static <T> LimaDataWatcher<Optional<T>> keepNullableSynced(Supplier<? extends NetworkSerializer<Optional<T>>> typeSupplier, Supplier<@Nullable T> getter, Consumer<@Nullable T> setter)
    {
        return keepNullableSynced(typeSupplier.get(), getter, setter);
    }

    public static <E extends Enum<E>> LimaDataWatcher<Integer> keepEnumSynced(Class<E> enumClass, Supplier<E> getter, Consumer<E> setter)
    {
        return new EnumWatcher<>(enumClass, getter, setter);
    }

    public static LimaDataWatcher<ItemStack> keepItemSynced(Supplier<ItemStack> getter, Consumer<ItemStack> setter)
    {
        return new ItemStackValue(getter, setter);
    }

    public static LimaDataWatcher<Integer> keepClientsideEntitySynced(Supplier<@Nullable Entity> getter, Consumer<@Nullable Entity> setter)
    {
        return keepSynced(LimaCoreNetworkSerializers.VAR_INT, () -> {
            Entity entity = getter.get();
            return (entity != null && !entity.isRemoved()) ? entity.getId() : -1;
        }, eid -> setter.accept(LimaCoreClientUtil.getClientEntity(eid)));
    }

    private T previousData;

    private AutomaticDataWatcher(NetworkSerializer<T> serializer)
    {
        super(serializer);
    }

    protected boolean areDataValuesEqual(T previousData, T currentData)
    {
        return previousData.equals(currentData);
    }

    private boolean checkForChanges()
    {
        if (previousData == null)
        {
            previousData = getCurrentData();
            return true;
        }

        T currentData = getCurrentData();

        if (!areDataValuesEqual(previousData, currentData))
        {
            previousData = currentData;
            return true;
        }

        return false;
    }

    @Override
    protected boolean tick()
    {
        if (checkForChanges()) setChanged(true);

        return super.tick();
    }

    private static sealed class SimpleValue<T> extends AutomaticDataWatcher<T>
    {
        private final Supplier<T> getter;
        private final Consumer<T> setter;

        private SimpleValue(NetworkSerializer<T> serializer, Supplier<T> getter, Consumer<T> setter)
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

    private static final class NullableValue<T> extends AutomaticDataWatcher<Optional<T>>
    {
        private final Supplier<@Nullable T> getter;
        private final Consumer<@Nullable T> setter;

        private NullableValue(NetworkSerializer<Optional<T>> serializer, Supplier<@Nullable T> getter, Consumer<@Nullable T> setter)
        {
            super(serializer);
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        protected Optional<T> getCurrentData()
        {
            return Optional.ofNullable(getter.get());
        }

        @Override
        protected void setCurrentData(Optional<T> currentData)
        {
            setter.accept(currentData.orElse(null));
        }
    }

    private static final class EnumWatcher<E extends Enum<E>> extends AutomaticDataWatcher<Integer>
    {
        private final Class<E> enumClass;
        private final Supplier<E> getter;
        private final Consumer<E> setter;

        private EnumWatcher(Class<E> enumClass, Supplier<E> getter, Consumer<E> setter)
        {
            super(LimaCoreNetworkSerializers.VAR_INT.get());
            this.enumClass = enumClass;
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        protected Integer getCurrentData()
        {
            return getter.get().ordinal();
        }

        @Override
        protected void setCurrentData(Integer currentData)
        {
            setter.accept(LimaCollectionsUtil.getEnumByOrdinal(enumClass, currentData));
        }
    }

    private static final class ItemStackValue extends SimpleValue<ItemStack>
    {
        private ItemStackValue(Supplier<ItemStack> getter, Consumer<ItemStack> setter)
        {
            super(LimaCoreNetworkSerializers.ITEM_STACK.get(), getter, setter);
        }

        @Override
        protected ItemStack getCurrentData()
        {
            return super.getCurrentData().copy();
        }

        @Override
        protected boolean areDataValuesEqual(ItemStack previousData, ItemStack currentData)
        {
            return ItemStack.matches(previousData, currentData);
        }
    }
}