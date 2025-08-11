package liedge.limacore.capability.energy;

import net.neoforged.neoforge.common.MutableDataComponentHolder;

import static liedge.limacore.registry.game.LimaCoreDataComponents.*;

public class LimaComponentEnergyStorage extends LimaEnergyStorage
{
    public static LimaComponentEnergyStorage createFromItem(MutableDataComponentHolder dataHolder, int defaultCapacity, int defaultTransferRate)
    {
        return new LimaComponentEnergyStorage(dataHolder,
                dataHolder.getOrDefault(ENERGY_CAPACITY, defaultCapacity),
                dataHolder.getOrDefault(ENERGY_TRANSFER_RATE, defaultTransferRate));
    }

    private final MutableDataComponentHolder dataHolder;

    public LimaComponentEnergyStorage(MutableDataComponentHolder dataHolder, int maxEnergy, int transferRate)
    {
        super(maxEnergy, transferRate);
        this.dataHolder = dataHolder;
    }

    @Override
    public void setEnergyStored(int energy)
    {
        dataHolder.set(ENERGY, Math.max(energy, 0));
    }

    @Override
    public int getEnergyStored()
    {
        return dataHolder.getOrDefault(ENERGY, 0);
    }
}