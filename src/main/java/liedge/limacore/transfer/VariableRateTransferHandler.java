package liedge.limacore.transfer;

import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;

public interface VariableRateTransferHandler
{
    int getTransferRate();

    void setTransferRate(int transferRate);

    default LimaDataWatcher<Integer> syncTransferRate()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, this::getTransferRate, this::setTransferRate);
    }
}