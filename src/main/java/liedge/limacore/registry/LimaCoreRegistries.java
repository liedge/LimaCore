package liedge.limacore.registry;

import liedge.limacore.network.NetworkSerializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import static liedge.limacore.LimaCore.RESOURCES;

public final class LimaCoreRegistries
{
    private LimaCoreRegistries() {}

    public static final Registry<NetworkSerializer<?>> NETWORK_SERIALIZERS = RESOURCES.registryBuilder(Keys.NETWORK_SERIALIZERS).sync(true).create();

    public static final class Keys
    {
        private Keys() {}

        public static final ResourceKey<Registry<NetworkSerializer<?>>> NETWORK_SERIALIZERS = RESOURCES.registryResourceKey("network_serializer");
    }
}