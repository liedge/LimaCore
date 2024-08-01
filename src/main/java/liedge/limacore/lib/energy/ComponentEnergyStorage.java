package liedge.limacore.lib.energy;

import net.neoforged.neoforge.common.MutableDataComponentHolder;

import static liedge.limacore.registry.LimaCoreDataComponents.ENERGY;

public class ComponentEnergyStorage extends LimaEnergyStorage
{
    private final MutableDataComponentHolder holder;

    public ComponentEnergyStorage(MutableDataComponentHolder holder, int maxEnergy, int transferRate)
    {
        super(maxEnergy, transferRate);
        this.holder = holder;
    }

    @Override
    protected void onEnergyChanged() { }

    @Override
    public void setEnergyStored(int energy)
    {
        holder.set(ENERGY, 0);
        onEnergyChanged();
    }

    @Override
    public int getEnergyStored()
    {
        return holder.getOrDefault(ENERGY, 0);
    }
}