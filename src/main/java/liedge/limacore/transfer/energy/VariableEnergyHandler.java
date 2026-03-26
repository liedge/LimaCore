package liedge.limacore.transfer.energy;

import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.transfer.VariableRateTransferHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

public interface VariableEnergyHandler extends EnergyHandler, VariableRateTransferHandler
{
    void setCapacity(int capacity);

    default void keepAllPropertiesSynced(DataWatcherHolder.DataWatcherCollector collector) { }
}