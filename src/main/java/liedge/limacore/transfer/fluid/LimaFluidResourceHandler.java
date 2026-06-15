package liedge.limacore.transfer.fluid;

import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.DataWatcherHolder;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public interface LimaFluidResourceHandler extends ResourceHandler<FluidResource>, IndexModifier<FluidResource>
{
    int getTransferRate();

    void setTransferRate(int transferRate);

    int getCapacity();

    void setCapacity(int capacity);

    default void syncAllProperties(DataWatcherHolder.DataWatcherCollector collector) { }

    default LimaDataWatcher<Integer> syncCapacity()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getCapacity, this::setCapacity);
    }

    default LimaDataWatcher<Integer> syncTransferRate()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getTransferRate, this::setTransferRate);
    }
}