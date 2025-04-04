package liedge.limacore.capability.energy;

import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import net.neoforged.neoforge.energy.IEnergyStorage;

public abstract class LimaEnergyStorage implements IEnergyStorage
{
    private int maxEnergy;
    private int transferRate;

    protected LimaEnergyStorage(int maxEnergy, int transferRate)
    {
        setMaxEnergyStored(maxEnergy);
        setTransferRate(transferRate);
    }

    protected abstract void onEnergyChanged();

    public LimaDataWatcher<Integer> keepStoredEnergySynced()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getEnergyStored, this::setEnergyStored);
    }

    public LimaDataWatcher<Integer> keepCapacitySynced()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getMaxEnergyStored, this::setMaxEnergyStored);
    }

    public LimaDataWatcher<Integer> keepTransferRateSynced()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getTransferRate, this::setTransferRate);
    }

    public void keepAllPropertiesSynced(DataWatcherHolder.DataWatcherCollector collector)
    {
        collector.register(keepStoredEnergySynced());
        collector.register(keepCapacitySynced());
        collector.register(keepTransferRateSynced());
    }

    public int getTransferRate()
    {
        return transferRate;
    }

    public void setTransferRate(int transferRate)
    {
        this.transferRate = Math.max(1, transferRate);
    }

    public void setMaxEnergyStored(int maxEnergy)
    {
        this.maxEnergy = Math.max(1, maxEnergy);
    }

    public int receiveEnergy(int toReceive, boolean simulate, boolean ignoreLimit)
    {
        if (toReceive <= 0) return 0;

        final int limit = ignoreLimit ? Integer.MAX_VALUE : transferRate;
        int energy = getEnergyStored();
        int actuallyReceived = Math.min(maxEnergy - energy, Math.min(toReceive, limit));

        if (!simulate && actuallyReceived > 0)
        {
            setEnergyStored(energy + actuallyReceived);
        }

        return actuallyReceived;
    }

    public int extractEnergy(int toExtract, boolean simulate, boolean ignoreLimit)
    {
        if (toExtract <= 0) return 0;

        final int limit = ignoreLimit ? Integer.MAX_VALUE : transferRate;
        int energy = getEnergyStored();
        int actuallyExtracted = Math.min(energy, Math.min(toExtract, limit));

        if (!simulate && actuallyExtracted > 0)
        {
            setEnergyStored(energy - actuallyExtracted);
        }

        return actuallyExtracted;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate)
    {
        return receiveEnergy(toReceive, simulate, false);
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate)
    {
        return extractEnergy(toExtract, simulate, false);
    }

    public abstract void setEnergyStored(int energy);

    @Override
    public abstract int getEnergyStored();

    @Override
    public int getMaxEnergyStored()
    {
        return maxEnergy;
    }

    @Override
    public boolean canExtract()
    {
        return true;
    }

    @Override
    public boolean canReceive()
    {
        return true;
    }
}