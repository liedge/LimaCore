package liedge.limacore.capability.energy;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntityAccess;
import liedge.limacore.util.LimaNbtUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public interface EnergyHolderBlockEntity extends LimaBlockEntityAccess
{
    int getBaseEnergyCapacity();

    int getBaseEnergyTransferRate();

    LimaEnergyStorage getEnergyStorage();

    default void onEnergyChanged(int previousEnergy)
    {
        setChanged();
    }

    IOAccess getSideIOForEnergy(@Nullable Direction side);

    default @Nullable IEnergyStorage createEnergyIOWrapper(@Nullable Direction side)
    {
        IOAccess blockAccessLevel = getSideIOForEnergy(side);
        return blockAccessLevel.allowsConnection() ? new EnergyStorageIOWrapper(getEnergyStorage(), blockAccessLevel) : null;
    }

    default void loadEnergyStorage(CompoundTag tag, HolderLookup.Provider registries)
    {
        if (getEnergyStorage() instanceof LimaBlockEntityEnergyStorage serializable)
        {
            LimaNbtUtil.deserializeInt(serializable, registries, tag.get(LimaCommonConstants.KEY_ENERGY_CONTAINER));
        }
    }

    default void saveEnergyStorage(CompoundTag tag, HolderLookup.Provider registries)
    {
        if (getEnergyStorage() instanceof LimaBlockEntityEnergyStorage serializable)
        {
            tag.put(LimaCommonConstants.KEY_ENERGY_CONTAINER, serializable.serializeNBT(registries));
        }
    }
}