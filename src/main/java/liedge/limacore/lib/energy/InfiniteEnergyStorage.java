package liedge.limacore.lib.energy;

public final class InfiniteEnergyStorage extends LimaEnergyStorage
{
    public static final InfiniteEnergyStorage INFINITE_ENERGY_STORAGE = new InfiniteEnergyStorage();

    private InfiniteEnergyStorage()
    {
        super(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public void setTransferRate(int transferRate) { }

    @Override
    public void setEnergyStored(int energy) {}

    @Override
    public int getEnergyStored()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setMaxEnergyStored(int maxEnergy) { }

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
    protected void onEnergyChanged() {}

    @Override
    public boolean canReceive()
    {
        return false;
    }
}