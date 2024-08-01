package liedge.limacore.network.packet;

import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.network.sync.DataWatcherHolder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import static liedge.limacore.network.NetworkSerializer.REGISTRY_STREAM_CODEC;

public abstract class DataWatcherPacketBase<T> implements LimaPlayPacket.ClientboundOnly
{
    static <E, T extends DataWatcherPacketBase<E>> StreamCodec<RegistryFriendlyByteBuf, T> createStreamCodec(StreamDecoder<RegistryFriendlyByteBuf, T> decoder)
    {
        return StreamCodec.of((buf, pkt) -> pkt.encodePacket(buf), decoder);
    }

    private final int index;
    private final NetworkSerializer<T> serializer;
    private final T data;

    DataWatcherPacketBase(int index, NetworkSerializer<T> serializer, T data)
    {
        this.index = index;
        this.serializer = serializer;
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    DataWatcherPacketBase(RegistryFriendlyByteBuf net)
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

    @Override
    public final void onReceivedByClient(IPayloadContext context, Player localPlayer)
    {
        DataWatcherHolder holder = watcherContext();
        if (holder != null)
        {
            holder.receiveDataPacket(index, data);
        }
    }

    abstract void encodeWatcherContext(RegistryFriendlyByteBuf net);

    abstract @Nullable DataWatcherHolder watcherContext();
}