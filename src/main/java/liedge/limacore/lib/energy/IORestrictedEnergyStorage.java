package liedge.limacore.lib.energy;

import liedge.limacore.lib.IODirection;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class IORestrictedEnergyStorage implements IEnergyStorage
{
    private final LimaEnergyStorage source;
    private final IODirection ioDirection;

    public IORestrictedEnergyStorage(LimaEnergyStorage source, IODirection ioDirection)
    {
        this.source = source;
        this.ioDirection = ioDirection;
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
        return ioDirection.allowsOutput();
    }

    @Override
    public boolean canReceive()
    {
        return ioDirection.allowsInput();
    }
}