package liedge.limacore.network.sync;

import liedge.limacore.network.NetworkSerializer;
import org.jspecify.annotations.Nullable;

public abstract class ValueTracker<T> extends LimaDataWatcher<T>
{
    private boolean automatic = false;
    private @Nullable T value;

    protected ValueTracker(NetworkSerializer<T> serializer)
    {
        super(serializer);
    }

    public ValueTracker<T> setAutomatic()
    {
        this.automatic = true;
        return this;
    }

    @Override
    protected boolean tick()
    {
        if (automatic) checkForChanges();

        return super.tick();
    }

    public void checkForChanges()
    {
        if (this.value == null)
        {
            this.value = getCurrentData();
            setChanged(true);
            return;
        }

        T nextValue = getCurrentData();
        if (!areDataValuesEqual(value, nextValue))
        {
            this.value = nextValue;
            setChanged(true);
        }
    }

    protected boolean areDataValuesEqual(T value, T nextValue)
    {
        return value.equals(nextValue);
    }
}