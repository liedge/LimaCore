package liedge.limacore.lib.energy;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.IntTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class BlockEnergyStorage extends LimaEnergyStorage implements INBTSerializable<IntTag>
{
    private final EnergyStorageHolder holder;
    private int energy;

    public BlockEnergyStorage(EnergyStorageHolder holder, int maxEnergy, int transferRate)
    {
        super(maxEnergy, transferRate);
        this.holder = holder;
    }

    @Override
    protected void onEnergyChanged()
    {
        holder.onEnergyChanged();
    }

    @Override
    public void setEnergyStored(int energy)
    {
        this.energy = energy;
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