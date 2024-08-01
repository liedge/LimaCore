package liedge.limacore.network.sync;

import liedge.limacore.network.NetworkSerializer;

import java.util.List;

public interface DataWatcherHolder
{
    List<LimaDataWatcher<?>> getDataWatchers();

    <T> void sendDataWatcherPacket(int index, NetworkSerializer<T> streamCodec, T data);

    @SuppressWarnings("unchecked")
    default <T> void receiveDataPacket(int index, T data)
    {
        LimaDataWatcher<T> dataWatcher = (LimaDataWatcher<T>) getDataWatchers().get(index);
        dataWatcher.setCurrentData(data);
    }

    default void tickDataWatchers(boolean forceUpdate)
    {
        List<LimaDataWatcher<?>> dataWatchers = getDataWatchers();
        for (int i = 0; i < dataWatchers.size(); i++)
        {
            dataWatchers.get(i).tickDataWatcher(i, this, forceUpdate);
        }
    }
}