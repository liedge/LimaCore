package liedge.limacore.registry.game;

import it.unimi.dsi.fastutil.ints.IntList;
import liedge.limacore.LimaCore;
import liedge.limacore.blockentity.RelativeHorizontalSide;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.registry.LimaDeferredNetworkSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Optional;

public final class LimaCoreNetworkSerializers
{
    private LimaCoreNetworkSerializers() {}

    private static final LimaDeferredNetworkSerializers SERIALIZERS = LimaCore.RESOURCES.deferredNetworkSerializers();

    public static void register(IEventBus modBus)
    {
        SERIALIZERS.register(modBus);
    }

    // Primitive data types
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Boolean>> BOOL = SERIALIZERS.registerCodec("bool", () -> ByteBufCodecs.BOOL);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Byte>> BYTE = SERIALIZERS.registerCodec("byte", () -> ByteBufCodecs.BYTE);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Integer>> VAR_INT = SERIALIZERS.registerCodec("var_int", () -> ByteBufCodecs.VAR_INT);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Float>> FLOAT = SERIALIZERS.registerCodec("float", () -> ByteBufCodecs.FLOAT);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Double>> DOUBLE = SERIALIZERS.registerCodec("double", () -> ByteBufCodecs.DOUBLE);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<String>> STRING_UTF8 = SERIALIZERS.registerCodec("string_utf8", () -> ByteBufCodecs.STRING_UTF8);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Unit>> UNIT = SERIALIZERS.registerCodec("unit", () -> LimaStreamCodecs.UNIT);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<IntList>> INT_LIST = SERIALIZERS.registerCodec("int_list", () -> LimaStreamCodecs.INT_LIST);

    // Common data objects
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<ResourceLocation>> RESOURCE_LOCATION = SERIALIZERS.registerCodec("rl", () -> ResourceLocation.STREAM_CODEC);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Optional<ResourceLocation>>> OPTIONAL_RESOURCE_LOCATION = SERIALIZERS.registerCodec("optional_rl", () -> ResourceLocation.STREAM_CODEC.apply(LimaStreamCodecs.asOptional()));
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<BlockPos>> BLOCK_POS = SERIALIZERS.registerCodec("block_pos", () -> BlockPos.STREAM_CODEC);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<ItemStack>> ITEM_STACK = SERIALIZERS.registerCodec("item_stack", () -> ItemStack.OPTIONAL_STREAM_CODEC);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<FluidStack>> FLUID_STACK = SERIALIZERS.registerCodec("fluid_stack", () -> FluidStack.OPTIONAL_STREAM_CODEC);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<Direction>> DIRECTION = SERIALIZERS.registerCodec("direction", () -> Direction.STREAM_CODEC);
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<RelativeHorizontalSide>> RELATIVE_SIDE = SERIALIZERS.registerCodec("relative_side", () -> RelativeHorizontalSide.STREAM_CODEC);

    // NBT tags
    public static final DeferredHolder<NetworkSerializer<?>, NetworkSerializer<CompoundTag>> COMPOUND_TAG = SERIALIZERS.registerCodec("compound_tag", () -> ByteBufCodecs.COMPOUND_TAG);
}