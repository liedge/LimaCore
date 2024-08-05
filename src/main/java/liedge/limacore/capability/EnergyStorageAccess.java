package liedge.limacore.capability;

import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyStorageAccess implements IEnergyStorage
{
    private final IEnergyStorage source;
    private final IOAccess ioAccess;

    public EnergyStorageAccess(IEnergyStorage source, IOAccess ioAccess)
    {
        this.source = source;
        this.ioAccess = ioAccess;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate)
    {
        return canReceive() ? source.receiveEnergy(toReceive, simulate) : 0;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate)
    {
        return canExtract() ? source.extractEnergy(toExtract, simulate) : 0;
    }

    @Override
    public int getEnergyStored()
    {
        return source.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored()
    {
        return source.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract()
    {
        return ioAccess.isOutputEnabled();
    }

    @Override
    public boolean canReceive()
    {
        return ioAccess.isInputEnabled();
    }
}