package liedge.limacore.lib.energy;

public interface EnergyStorageHolder
{
    LimaEnergyStorage getEnergyStorage();

    void onEnergyChanged();
}