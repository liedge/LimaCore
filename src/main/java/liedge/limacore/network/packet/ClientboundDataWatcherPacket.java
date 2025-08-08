package liedge.limacore.network.packet;

import liedge.limacore.network.ClientboundPayload;
import liedge.limacore.network.NetworkSerializer;
import net.minecraft.network.RegistryFriendlyByteBuf;

import static liedge.limacore.network.NetworkSerializer.REGISTRY_STREAM_CODEC;

public abstract class ClientboundDataWatcherPacket<T> implements ClientboundPayload
{
    private final int index;
    private final NetworkSerializer<T> serializer;
    private final T data;

    ClientboundDataWatcherPacket(int index, NetworkSerializer<T> serializer, T data)
    {
        this.index = index;
        this.serializer = serializer;
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    ClientboundDataWatcherPacket(RegistryFriendlyByteBuf net)
    {
        this.index = net.readVarInt();
        this.serializer = (NetworkSerializer<T>) REGISTRY_STREAM_CODEC.decode(net);
        this.data = serializer.streamCodec().decode(net); //codec.decode(net);
    }

    void encodePacket(RegistryFriendlyByteBuf net)
    {
        net.writeVarInt(index);
        REGISTRY_STREAM_CODEC.encode(net, serializer);
        serializer.streamCodec().encode(net, data);
        encodeWatcherContext(net);
    }

    abstract void encodeWatcherContext(RegistryFriendlyByteBuf net);

    public int getIndex()
    {
        return index;
    }

    public T getData()
    {
        return data;
    }
}