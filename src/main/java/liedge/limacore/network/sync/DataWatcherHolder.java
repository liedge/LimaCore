package liedge.limacore.network.sync;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.util.LimaCollectionsUtil;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public interface DataWatcherHolder
{
    List<LimaDataWatcher<?>> getDataWatchers();

    @ApiStatus.OverrideOnly
    <T> void sendDataWatcherPacket(int index, NetworkSerializer<T> serializer, T data);

    @ApiStatus.OverrideOnly
    void defineDataWatchers(DataWatcherCollector collector);

    @ApiStatus.Internal
    @SuppressWarnings("unchecked")
    default <T> void receiveDataPacket(int index, T data)
    {
        LimaDataWatcher<T> dataWatcher = (LimaDataWatcher<T>) getDataWatchers().get(index);
        dataWatcher.setCurrentData(data);
    }

    default void forceSyncDataWatchers()
    {
        List<LimaDataWatcher<?>> dataWatchers = getDataWatchers();
        for (int i = 0; i < dataWatchers.size(); i++)
        {
            dataWatchers.get(i).sendDataPacket(i, this);
        }
    }

    default void tickDataWatchers()
    {
        List<LimaDataWatcher<?>> dataWatchers = getDataWatchers();
        for (int i = 0; i < dataWatchers.size(); i++)
        {
            dataWatchers.get(i).tickWatcher(i, this);
        }
    }

    default List<LimaDataWatcher<?>> createDataWatchers()
    {
        ObjectList<LimaDataWatcher<?>> list = new ObjectArrayList<>();
        defineDataWatchers(watcher -> LimaCollectionsUtil.addAndGetIndex(list, watcher));
        return ObjectLists.unmodifiable(list);
    }

    @FunctionalInterface
    interface DataWatcherCollector
    {
        int register(LimaDataWatcher<?> watcher);
    }
}