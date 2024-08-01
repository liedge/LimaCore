package liedge.limacore.network.sync;

import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SimpleDataWatcher<T> extends LimaDataWatcher<T>
{
    public static <T> LimaDataWatcher<T> keepSynced(NetworkSerializer<T> networkSerializer, Supplier<T> getter, Consumer<T> setter)
    {
        return new SimpleDataWatcher<>(networkSerializer, getter, setter);
    }

    public static <T> LimaDataWatcher<T> keepSynced(Supplier<? extends NetworkSerializer<T>> supplier, Supplier<T> getter, Consumer<T> setter)
    {
        return new SimpleDataWatcher<>(supplier.get(), getter, setter);
    }

    public static LimaDataWatcher<ItemStack> keepItemSynced(Supplier<ItemStack> getter, Consumer<ItemStack> setter)
    {
        return new ItemStackWatcher(getter, setter);
    }

    public static LimaDataWatcher<Optional<Entity>> keepOptionalRemoteEntitySynced(Supplier<@Nullable Entity> getter, Consumer<@Nullable Entity> setter)
    {
        return keepSynced(LimaCoreNetworkSerializers.REMOTE_ENTITY, () -> Optional.ofNullable(getter.get()), optional -> setter.accept(optional.orElse(null)));
    }

    private final Supplier<T> getter;
    private final Consumer<T> setter;

    private T previousData;

    protected SimpleDataWatcher(NetworkSerializer<T> networkSerializer, Supplier<T> getter, Consumer<T> setter)
    {
        super(networkSerializer);
        this.getter = getter;
        this.setter = setter;
    }

    protected boolean areDataValuesEqual(T previousData, T currentData)
    {
        return previousData.equals(currentData);
    }

    @Override
    protected boolean hasDataChanged()
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
        else
        {
            return false;
        }
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

    private static class ItemStackWatcher extends SimpleDataWatcher<ItemStack>
    {
        protected ItemStackWatcher(Supplier<ItemStack> getter, Consumer<ItemStack> setter)
        {
            super(LimaCoreNetworkSerializers.ITEM_STACK.get(), getter, setter);
        }

        @Override
        protected boolean areDataValuesEqual(ItemStack previousData, ItemStack currentData)
        {
            return ItemStack.matches(previousData, currentData);
        }
    }
}