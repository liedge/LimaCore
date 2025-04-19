package liedge.limacore.capability.energy;

import net.neoforged.neoforge.common.MutableDataComponentHolder;

import static liedge.limacore.registry.game.LimaCoreDataComponents.ENERGY;

public class LimaComponentEnergyStorage extends LimaEnergyStorage
{
    private final MutableDataComponentHolder dataHolder;

    public LimaComponentEnergyStorage(MutableDataComponentHolder dataHolder, int maxEnergy, int transferRate)
    {
        super(maxEnergy, transferRate);
        this.dataHolder = dataHolder;
    }

    public LimaComponentEnergyStorage(MutableDataComponentHolder dataHolder, ItemEnergyProperties properties)
    {
        this(dataHolder, properties.capacity(), properties.transferRate());
    }

    @Override
    protected void onEnergyChanged() {}

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