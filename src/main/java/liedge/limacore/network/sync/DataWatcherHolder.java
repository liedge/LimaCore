package liedge.limacore.network.sync;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import liedge.limacore.network.IndexedStreamData;
import liedge.limacore.util.LimaCollectionsUtil;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public interface DataWatcherHolder
{
    List<LimaDataWatcher<?>> getDataWatchers();

    @ApiStatus.OverrideOnly
    void defineDataWatchers(DataWatcherCollector collector);

    @ApiStatus.OverrideOnly
    void sendDataWatcherPacket(List<IndexedStreamData<?>> streamData);

    @ApiStatus.Internal
    default void receiveDataWatcherPacket(List<IndexedStreamData<?>> streamData)
    {
        List<LimaDataWatcher<?>> dataWatchers = getDataWatchers();

        for (var entry : streamData)
        {
            dataWatchers.get(entry.index()).readStreamData(entry);
        }
    }

    default void forceSyncDataWatchers()
    {
        List<LimaDataWatcher<?>> dataWatchers = getDataWatchers();
        if (dataWatchers.isEmpty()) return;

        List<IndexedStreamData<?>> streamData = new ObjectArrayList<>();
        for (int index = 0; index < dataWatchers.size(); index++)
        {
            streamData.add(dataWatchers.get(index).writeStreamData(index));
        }
        sendDataWatcherPacket(streamData);
    }

    default void tickDataWatchers()
    {
        List<LimaDataWatcher<?>> dataWatchers = getDataWatchers();
        List<IndexedStreamData<?>> streamData = null;

        for (int index = 0; index < dataWatchers.size(); index++)
        {
            // Tick watchers
            LimaDataWatcher<?> watcher = dataWatchers.get(index);

            if (watcher.tick())
            {
                if (streamData == null) streamData = new ObjectArrayList<>();

                streamData.add(watcher.writeStreamData(index));
            }
        }

        if (streamData != null) sendDataWatcherPacket(streamData);
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