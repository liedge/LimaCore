package liedge.limacore.network;

import liedge.limacore.registry.LimaCoreRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record NetworkSerializer<T>(ResourceLocation id, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec)
{
    public static <T> NetworkSerializer<T> create(ResourceLocation id, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec)
    {
        return new NetworkSerializer<>(id, streamCodec);
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, NetworkSerializer<?>> REGISTRY_STREAM_CODEC = ByteBufCodecs.registry(LimaCoreRegistries.NETWORK_SERIALIZERS_KEY);

    @Override
    public String toString()
    {
        return "NetworkSerializer[" + id + "]";
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        else if (obj instanceof NetworkSerializer<?> serializer)
        {
            return this.id.equals(serializer.id);
        }
        else
        {
            return false;
        }
    }
}