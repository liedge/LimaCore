package liedge.limacore.capability.energy;

import liedge.limacore.blockentity.IOAccess;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyStorageIOWrapper implements IEnergyStorage
{
    private final IEnergyStorage parent;
    private final IOAccess ioAccess;

    public EnergyStorageIOWrapper(IEnergyStorage parent, IOAccess ioAccess)
    {
        this.parent = parent;
        this.ioAccess = ioAccess;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate)
    {
        return canReceive() ? parent.receiveEnergy(toReceive, simulate) : 0;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate)
    {
        return canExtract() ? parent.extractEnergy(toExtract, simulate) : 0;
    }

    @Override
    public int getEnergyStored()
    {
        return parent.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored()
    {
        return parent.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract()
    {
        return ioAccess.allowsOutput();
    }

    @Override
    public boolean canReceive()
    {
        return ioAccess.allowsInput();
    }
}