package liedge.limacore.network;

import liedge.limacore.LimaCore;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record IndexedStreamData<T>(int index, NetworkSerializer<T> serializer, T data)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, IndexedStreamData<?>> STREAM_CODEC = StreamCodec.of((net, obj) -> obj.encode(net), IndexedStreamData::decode);
    public static final StreamCodec<RegistryFriendlyByteBuf, List<IndexedStreamData<?>>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

    @SuppressWarnings("unchecked")
    private static <T> IndexedStreamData<T> decode(RegistryFriendlyByteBuf net)
    {
        int index = net.readVarInt();
        NetworkSerializer<T> serializer = (NetworkSerializer<T>) NetworkSerializer.REGISTRY_STREAM_CODEC.decode(net);
        T data = serializer.decode(net);

        return new IndexedStreamData<>(index, serializer, data);
    }

    private void encode(RegistryFriendlyByteBuf net)
    {
        net.writeVarInt(index);
        NetworkSerializer.REGISTRY_STREAM_CODEC.encode(net, serializer);
        serializer.encode(net, data);
    }

    @SuppressWarnings("unchecked")
    public <A> @Nullable A tryCast(NetworkSerializer<A> other)
    {
        if (other == serializer)
        {
            return (A) data;
        }
        else
        {
            LimaCore.LOGGER.warn("Attempted to access decoded data with mismatching serializers. Expected {} but received {}.", other.id(), serializer.id());
            return null;
        }
    }
}