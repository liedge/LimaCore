package liedge.limacore.capability.energy;

public interface EnergyStorageHolder
{
    LimaEnergyStorage getEnergyStorage();

    void onEnergyChanged();
}