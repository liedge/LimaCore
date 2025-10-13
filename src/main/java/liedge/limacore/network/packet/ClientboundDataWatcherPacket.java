package liedge.limacore.network.packet;

import liedge.limacore.network.ClientboundPayload;
import liedge.limacore.network.sync.DataWatcherHolder;

import java.util.List;

public interface ClientboundDataWatcherPacket extends ClientboundPayload
{
    List<DataWatcherHolder.DataEntry<?>> entries();
}