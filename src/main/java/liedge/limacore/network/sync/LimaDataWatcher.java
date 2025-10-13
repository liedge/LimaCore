package liedge.limacore.network.sync;

import liedge.limacore.network.NetworkSerializer;
import org.jetbrains.annotations.ApiStatus;

public abstract sealed class LimaDataWatcher<T> permits ManualDataWatcher, AutomaticDataWatcher
{
    private final NetworkSerializer<T> serializer;
    private boolean changed;

    protected LimaDataWatcher(NetworkSerializer<T> serializer)
    {
        this.serializer = serializer;
    }

    public boolean isChanged()
    {
        return changed;
    }

    public void setChanged(boolean changed)
    {
        this.changed = changed;
    }

    protected abstract T getCurrentData();

    protected abstract void setCurrentData(T currentData);

    @ApiStatus.Internal
    protected boolean tick()
    {
        if (isChanged())
        {
            setChanged(false);
            return true;
        }

        return false;
    }

    DataWatcherHolder.DataEntry<T> writeDataEntry(int index)
    {
        return new DataWatcherHolder.DataEntry<>(index, serializer, getCurrentData());
    }

    @SuppressWarnings("unchecked")
    void readDataEntry(DataWatcherHolder.DataEntry<?> entry)
    {
        setCurrentData((T) entry.data());
    }
}