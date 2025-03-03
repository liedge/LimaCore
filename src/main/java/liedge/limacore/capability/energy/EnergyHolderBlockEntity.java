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

    IOAccess getEnergyIOForSide(Direction side);

    default @Nullable IEnergyStorage createEnergyIOWrapper(@Nullable Direction side)
    {
        if (side != null)
        {
            return new EnergyStorageIOWrapper(getEnergyStorage(), getEnergyIOForSide(side));
        }

        return null;
    }
}