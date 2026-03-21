package liedge.limacore.transfer.energy;

import liedge.limacore.transfer.VariableRateTransferHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

public interface VariableEnergyHandler extends EnergyHandler, VariableRateTransferHandler
{
    void setCapacity(int capacity);
}