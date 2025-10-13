package liedge.limacore.registry;

import liedge.limacore.network.NetworkSerializer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class LimaDeferredNetworkSerializers extends DeferredRegister<NetworkSerializer<?>>
{
    public static LimaDeferredNetworkSerializers create(String namespace)
    {
        return new LimaDeferredNetworkSerializers(namespace);
    }

    private LimaDeferredNetworkSerializers(String namespace)
    {
        super(LimaCoreRegistries.Keys.NETWORK_SERIALIZERS, namespace);
    }

    public <T> DeferredHolder<NetworkSerializer<?>, NetworkSerializer<T>> registerCodec(String name, Supplier<StreamCodec<? super RegistryFriendlyByteBuf, T>> supplier)
    {
        return register(name, id -> NetworkSerializer.create(id, supplier.get()));
    }
}