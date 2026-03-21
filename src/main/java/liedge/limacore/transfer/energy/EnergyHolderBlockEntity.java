package liedge.limacore.transfer.energy;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntityAccess;
import net.minecraft.core.Direction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public interface EnergyHolderBlockEntity extends LimaBlockEntityAccess
{
    VariableEnergyHandler getEnergy();

    int getBaseEnergyCapacity();

    int getBaseEnergyTransferRate();

    default IOAccess getTopLevelEnergyIO(@Nullable Direction side)
    {
        return IOAccess.DISABLED;
    }

    @ApiStatus.OverrideOnly
    default void onEnergyChanged(int previousAmount)
    {
        setChanged();
    }

    default @Nullable EnergyHandler createExternalEnergy(@Nullable Direction side)
    {
        IOAccess access = getTopLevelEnergyIO(side);
        return access.allowsConnection() ? new ExternalEnergyHandler(getEnergy(), access) : null;
    }

    default void loadEnergyStorage(ValueInput input)
    {
        if (getEnergy() instanceof ValueIOSerializable energy)
        {
            energy.deserialize(input);
        }
    }

    default void saveEnergyStorage(ValueOutput output)
    {
        if (getEnergy() instanceof ValueIOSerializable energy)
        {
            energy.serialize(output);
        }
    }
}