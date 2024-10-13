package liedge.limacore.capability.energy;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.IntTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class LimaBlockEntityEnergyStorage extends LimaEnergyStorage implements INBTSerializable<IntTag>
{
    private final EnergyHolderBlockEntity energyHolder;
    private int energy;

    public LimaBlockEntityEnergyStorage(EnergyHolderBlockEntity energyHolder, int maxEnergy, int transferRate)
    {
        super(maxEnergy, transferRate);
        this.energyHolder = energyHolder;
    }

    @Override
    protected void onEnergyChanged()
    {
        energyHolder.onEnergyChanged();
    }

    @Override
    public void setEnergyStored(int energy)
    {
        this.energy = Math.max(energy, 0);
        onEnergyChanged();
    }

    @Override
    public int getEnergyStored()
    {
        return energy;
    }

    @Override
    public IntTag serializeNBT(HolderLookup.Provider provider)
    {
        return IntTag.valueOf(energy);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, IntTag nbt)
    {
        energy = nbt.getAsInt();
    }
}