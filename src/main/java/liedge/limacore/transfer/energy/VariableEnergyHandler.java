package liedge.limacore.transfer.energy;

import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.transfer.VariableRateTransferHandler;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

public interface VariableEnergyHandler extends EnergyHandler, VariableRateTransferHandler
{
    void setCapacity(int capacity);

    default void keepAllPropertiesSynced(DataWatcherHolder.DataWatcherCollector collector) { }

    default void readComponents(DataComponentGetter components) { }

    default void writeComponents(DataComponentMap.Builder components) { }
}