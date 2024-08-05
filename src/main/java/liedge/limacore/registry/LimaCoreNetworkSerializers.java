package liedge.limacore.registry;

import liedge.limacore.LimaCore;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.network.NetworkSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

public final class LimaCoreNetworkSerializers
{
    private LimaCoreNetworkSerializers() {}

    private static final DeferredRegister<NetworkSerializer<?>> SERIALIZERS = LimaCore.RESOURCES.deferredRegister(LimaCoreRegistries.NETWORK_SERIALIZERS_KEY);

    public static void initRegister(IEventBus modBus)
    {
        SERIALIZERS.register(modBus);
    }

    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Boolean>> BOOL = SERIALIZERS.register("bool", id -> NetworkSerializer.create(id, ByteBufCodecs.BOOL));
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Byte>> BYTE = SERIALIZERS.register("byte", id -> NetworkSerializer.create(id, ByteBufCodecs.BYTE));
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Integer>> VAR_INT = SERIALIZERS.register("var_int", id -> NetworkSerializer.create(id, ByteBufCodecs.VAR_INT));
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Float>> FLOAT = SERIALIZERS.register("float", id -> NetworkSerializer.create(id, ByteBufCodecs.FLOAT));
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Double>> DOUBLE = SERIALIZERS.register("double", id -> NetworkSerializer.create(id, ByteBufCodecs.DOUBLE));
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<ResourceLocation>> RESOURCE_LOCATION = SERIALIZERS.register("rl", id -> NetworkSerializer.create(id, ResourceLocation.STREAM_CODEC));
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Optional<ResourceLocation>>> OPTIONAL_RESOURCE_LOCATION = SERIALIZERS.register("optional_rl", id -> NetworkSerializer.create(id, ResourceLocation.STREAM_CODEC.apply(LimaStreamCodecs.asOptional())));
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<BlockPos>> BLOCK_POS = SERIALIZERS.register("block_pos", id -> NetworkSerializer.create(id, BlockPos.STREAM_CODEC));
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<ItemStack>> ITEM_STACK = SERIALIZERS.register("item_stack", id -> NetworkSerializer.create(id, ItemStack.OPTIONAL_STREAM_CODEC));
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Optional<Entity>>> REMOTE_ENTITY = SERIALIZERS.register("remote_entity", id -> NetworkSerializer.create(id, LimaStreamCodecs.REMOTE_ENTITY));
}