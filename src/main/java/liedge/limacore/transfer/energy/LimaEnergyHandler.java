package liedge.limacore.transfer.energy;

import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

public interface LimaEnergyHandler extends EnergyHandler
{
    int getTransferRate();

    void setTransferRate(int transferRate);

    default void setCapacity(int capacity) { }

    default void set(int energy) { }

    default void readComponents(DataComponentGetter components) { }

    default void writeComponents(DataComponentMap.Builder components) { }

    default void syncAllProperties(DataWatcherHolder.DataWatcherCollector collector) { }

    default LimaDataWatcher<Integer> syncEnergy()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getAmountAsInt, this::set);
    }

    default LimaDataWatcher<Integer> syncCapacity()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getCapacityAsInt, this::setCapacity);
    }

    default LimaDataWatcher<Integer> syncTransferRate()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getTransferRate, this::setTransferRate);
    }
}