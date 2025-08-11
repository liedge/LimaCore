package liedge.limacore.capability.energy;

public final class InfiniteEnergyStorage extends LimaEnergyStorage
{
    public static final InfiniteEnergyStorage INFINITE_ENERGY_STORAGE = new InfiniteEnergyStorage();

    private InfiniteEnergyStorage()
    {
        super(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public void setTransferRate(int transferRate)
    {
        super.setTransferRate(Integer.MAX_VALUE);
    }

    @Override
    public int getTransferRate()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setEnergyStored(int energy) { }

    @Override
    public int getEnergyStored()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setMaxEnergyStored(int maxEnergy)
    {
        super.setMaxEnergyStored(Integer.MAX_VALUE);
    }

    @Override
    public int getMaxEnergyStored()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate, boolean ignoreLimit)
    {
        return 0;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate, boolean ignoreLimit)
    {
        return toExtract;
    }

    @Override
    public boolean canReceive()
    {
        return false;
    }
}