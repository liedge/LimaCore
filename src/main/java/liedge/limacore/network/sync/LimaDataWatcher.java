package liedge.limacore.network.sync;

import liedge.limacore.network.NetworkSerializer;
import org.jetbrains.annotations.ApiStatus;

public abstract class LimaDataWatcher<T>
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
    protected void sendDataPacket(int index, DataWatcherHolder holder)
    {
        holder.sendDataWatcherPacket(index, serializer, getCurrentData());
    }

    @ApiStatus.Internal
    protected void tickWatcher(int index, DataWatcherHolder holder)
    {
        if (isChanged())
        {
            sendDataPacket(index, holder);
            setChanged(false);
        }
    }
}