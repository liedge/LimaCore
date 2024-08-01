package liedge.limacore.network.sync;

import liedge.limacore.network.NetworkSerializer;

public abstract class LimaDataWatcher<T>
{
    private final NetworkSerializer<T> networkSerializer;

    protected LimaDataWatcher(NetworkSerializer<T> networkSerializer)
    {
        this.networkSerializer = networkSerializer;
    }

    protected abstract boolean hasDataChanged();

    protected abstract T getCurrentData();

    protected abstract void setCurrentData(T currentData);

    void tickDataWatcher(int index, DataWatcherHolder holder, boolean forceUpdate)
    {
        if (hasDataChanged() || forceUpdate)
        {
            T data = getCurrentData();
            holder.sendDataWatcherPacket(index, networkSerializer, data);
        }
    }
}