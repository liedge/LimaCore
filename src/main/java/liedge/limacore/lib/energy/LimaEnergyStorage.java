package liedge.limacore.lib.energy;

import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.network.sync.SimpleDataWatcher;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import net.neoforged.neoforge.energy.IEnergyStorage;

public abstract class LimaEnergyStorage implements IEnergyStorage
{
    private int maxEnergy;
    private int transferRate;

    protected LimaEnergyStorage(int maxEnergy, int transferRate)
    {
        this.maxEnergy = Math.max(maxEnergy, 1);
        this.transferRate = Math.max(transferRate, 1);
    }

    protected abstract void onEnergyChanged();

    public LimaDataWatcher<Integer> createDataWatcher()
    {
        return SimpleDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getEnergyStored, this::setEnergyStored);
    }

    public int getTransferRate()
    {
        return transferRate;
    }

    public void setTransferRate(int transferRate)
    {
        this.transferRate = transferRate;
        onEnergyChanged();
    }

    public void setMaxEnergyStored(int maxEnergy)
    {
        this.maxEnergy = maxEnergy;
        onEnergyChanged();
    }

    public int receiveEnergy(int toReceive, boolean simulate, boolean ignoreLimit)
    {
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