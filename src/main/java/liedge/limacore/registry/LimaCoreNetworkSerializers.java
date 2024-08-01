package liedge.limacore.registry;

import liedge.limacore.LimaCore;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.network.NetworkSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
import java.util.function.Supplier;

public final class LimaCoreNetworkSerializers
{
    private LimaCoreNetworkSerializers() {}

    private static final DeferredRegister<NetworkSerializer<?>> DEFERRED_REGISTER = LimaCore.RESOURCES.deferredRegister("net_serializers");
    public static final Registry<NetworkSerializer<?>> NETWORK_SERIALIZERS = DEFERRED_REGISTER.makeRegistry(builder -> builder.sync(true));

    public static void initRegister(IEventBus modBus)
    {
        DEFERRED_REGISTER.register(modBus);
    }

    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Boolean>> BOOL = register("bool", () -> ByteBufCodecs.BOOL);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Byte>> BYTE = register("byte", () -> ByteBufCodecs.BYTE);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Integer>> VAR_INT = register("var_int", () -> ByteBufCodecs.VAR_INT);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Float>> FLOAT = register("float", () -> ByteBufCodecs.FLOAT);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Double>> DOUBLE = register("double", () -> ByteBufCodecs.DOUBLE);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<ResourceLocation>> RESOURCE_LOCATION = register("rl", () -> ResourceLocation.STREAM_CODEC);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<BlockPos>> BLOCK_POS = register("block_pos", () -> BlockPos.STREAM_CODEC);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<ItemStack>> ITEM_STACK = register("item_stack", () -> ItemStack.OPTIONAL_STREAM_CODEC);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Optional<Entity>>> REMOTE_ENTITY = register("remote_entity", () -> LimaStreamCodecs.REMOTE_ENTITY);

    private static <T> DeferredHolder<NetworkSerializer<?>, NetworkSerializer<T>> register(String name, Supplier<StreamCodec<? super RegistryFriendlyByteBuf, T>> codecSupplier)
    {
        return DEFERRED_REGISTER.register(name, id -> new NetworkSerializer<>(id, codecSupplier.get()));
    }
}