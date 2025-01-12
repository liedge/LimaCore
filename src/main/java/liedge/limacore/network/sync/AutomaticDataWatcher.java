package liedge.limacore.network.sync;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaCollectionsUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AutomaticDataWatcher<T> extends ManualDataWatcher<T>
{
    public static <T> LimaDataWatcher<T> keepSynced(NetworkSerializer<T> serializer, Supplier<T> getter, Consumer<T> setter)
    {
        return new AutomaticDataWatcher<>(serializer, getter, setter);
    }

    public static <T> LimaDataWatcher<T> keepSynced(Supplier<? extends NetworkSerializer<T>> supplier, Supplier<T> getter, Consumer<T> setter)
    {
        return new AutomaticDataWatcher<>(supplier.get(), getter, setter);
    }

    public static <E extends Enum<E>> LimaDataWatcher<Integer> keepEnumSynced(Class<E> enumClass, Supplier<E> getter, Consumer<E> setter)
    {
        return new AutomaticDataWatcher<>(LimaCoreNetworkSerializers.VAR_INT.get(), () -> getter.get().ordinal(), ordinal -> setter.accept(LimaCollectionsUtil.getEnumByOrdinal(enumClass, ordinal)));
    }

    public static LimaDataWatcher<ItemStack> keepItemSynced(Supplier<ItemStack> getter, Consumer<ItemStack> setter)
    {
        return new ItemWatcher(getter, setter);
    }

    public static LimaDataWatcher<Integer> keepClientsideEntitySynced(Supplier<@Nullable Entity> getter, Consumer<@Nullable Entity> setter)
    {
        return keepSynced(LimaCoreNetworkSerializers.VAR_INT, () -> {
            Entity entity = getter.get();
            return (entity != null && !entity.isRemoved()) ? entity.getId() : -1;
        }, eid -> setter.accept(LimaCoreClientUtil.getClientEntity(eid)));
    }

    private T previousData;

    protected AutomaticDataWatcher(NetworkSerializer<T> serializer, Supplier<T> getter, Consumer<T> setter)
    {
        super(serializer, getter, setter);
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
    protected void tickWatcher(int index, DataWatcherHolder holder)
    {
        if (checkForChanges()) setChanged(true);

        super.tickWatcher(index, holder);
    }

    private static class ItemWatcher extends AutomaticDataWatcher<ItemStack>
    {
        private ItemWatcher(Supplier<ItemStack> getter, Consumer<ItemStack> setter)
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