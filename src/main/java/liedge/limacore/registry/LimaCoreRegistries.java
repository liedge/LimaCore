package liedge.limacore.registry;

import liedge.limacore.LimaCore;
import liedge.limacore.network.NetworkSerializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public final class LimaCoreRegistries
{
    private LimaCoreRegistries() {}

    public static final ResourceKey<Registry<NetworkSerializer<?>>> NETWORK_SERIALIZERS_KEY = LimaCore.RESOURCES.registryResourceKey("network_serializers");
    public static final Registry<NetworkSerializer<?>> NETWORK_SERIALIZERS = LimaCore.RESOURCES.registryBuilder(NETWORK_SERIALIZERS_KEY).sync(true).create();
}