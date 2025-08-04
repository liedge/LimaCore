package liedge.limacore.capability.energy;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntityAccess;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public interface EnergyHolderBlockEntity extends LimaBlockEntityAccess
{
    int getBaseEnergyCapacity();

    int getBaseEnergyTransferRate();

    LimaEnergyStorage getEnergyStorage();

    default void onEnergyChanged()
    {
        setChanged();
    }

    IOAccess getSideIOForEnergy(@Nullable Direction side);

    default @Nullable IEnergyStorage createEnergyIOWrapper(@Nullable Direction side)
    {
        IOAccess blockAccessLevel = getSideIOForEnergy(side);
        return blockAccessLevel.allowsConnection() ? new EnergyStorageIOWrapper(getEnergyStorage(), blockAccessLevel) : null;
    }
}