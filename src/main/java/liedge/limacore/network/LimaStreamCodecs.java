package liedge.limacore.network;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.*;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.util.LimaCoreUtil;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;

public final class LimaStreamCodecs
{
    private LimaStreamCodecs() {}

    // Common registries
    public static final StreamCodec<RegistryFriendlyByteBuf, Item> ITEM_DIRECT = ByteBufCodecs.registry(Registries.ITEM);

    // Extra codecs
    public static final StreamCodec<ByteBuf, Unit> UNIT = NeoForgeStreamCodecs.uncheckedUnit(Unit.INSTANCE);
    public static final StreamCodec<ByteBuf, Integer> NON_NEGATIVE_VAR_INT = varIntRange(0, Integer.MAX_VALUE);
    public static final StreamCodec<ByteBuf, Integer> POSITIVE_VAR_INT = varIntRange(1, Integer.MAX_VALUE);
    public static final StreamCodec<ByteBuf, StringTag> STRING_NBT_TAG = ByteBufCodecs.STRING_UTF8.map(StringTag::valueOf, StringTag::getAsString);
    public static final StreamCodec<ByteBuf, Vec3> VEC3D = StreamCodec.of((net, vec) -> net.writeDouble(vec.x).writeDouble(vec.y).writeDouble(vec.z), net -> new Vec3(net.readDouble(), net.readDouble(), net.readDouble()));
    public static final StreamCodec<RegistryFriendlyByteBuf, List<SizedIngredient>> ITEM_INGREDIENTS_UNIT = StreamCodec.unit(List.of());
    public static final StreamCodec<RegistryFriendlyByteBuf, List<SizedFluidIngredient>> FLUID_INGREDIENTS_UNIT = StreamCodec.unit(List.of());
    public static final StreamCodec<RegistryFriendlyByteBuf, List<FluidStack>> FLUID_RESULTS_UNIT = StreamCodec.unit(List.of());

    public static final StreamCodec<ByteBuf, IntList> INT_LIST = new StreamCodec<>()
    {
        @Override
        public IntList decode(ByteBuf buffer)
        {
            int size = VarInt.read(buffer);
            if (size <= 0) return IntList.of();

            IntList list = new IntArrayList(size);

            for (int i = 0; i < size; i++)
            {
                list.add(VarInt.read(buffer));
            }

            return list;
        }

        @Override
        public void encode(ByteBuf buffer, IntList value)
        {
            VarInt.write(buffer, value.size());
            for (int i : value)
            {
                VarInt.write(buffer, i);
            }
        }
    };

    //#region Value encode/decode helpers
    public static int readClampedVarInt(ByteBuf net, int min, int max)
    {
        int value = VarInt.read(net);
        if (value < min || value > max) throw new DecoderException("Tried decoding value out of range [" + min + "," + max + "]");
        return value;
    }

    public static void writeClampedVarInt(ByteBuf net, int value, int min, int max)
    {
        if (value < min || value > max) throw new EncoderException("Tried encoding value out of range [" + min + "," + max + "]");
        VarInt.write(net, value);
    }

    public static float readClampedFloat(ByteBuf net, float min, float max)
    {
        float value = net.readFloat();
        if (value < min || value > max) throw new DecoderException("Tried decoding value out of range [" + min + "," + max + "]");
        return value;
    }

    public static void writeClampedFloat(ByteBuf net, float value, float min, float max)
    {
        if (value < min || value > max) throw new EncoderException("Tried encoding value out of range [" + min + "," + max + "]");
        net.writeFloat(value);
    }

    public static double readClampedDouble(ByteBuf net, double min, double max)
    {
        double value = net.readDouble();
        if (value < min || value > max) throw new DecoderException("Tried decoding value out of range [" + min + "," + max + "]");
        return value;
    }

    public static void writeClampedDouble(ByteBuf net, double value, double min, double max)
    {
        if (value < min || value > max) throw new EncoderException("Tried encoding value out of range [" + min + "," + max + "]");
        net.writeDouble(value);
    }

    public static <B extends ByteBuf, E, C extends Collection<E>> C readClampedCollection(StreamDecoder<? super B, E> decoder, B buffer, IntFunction<C> collectionFactory, int min, int max)
    {
        int size = readClampedVarInt(buffer, min, max);
        C collection = collectionFactory.apply(size);
        for (int i = 0; i < size; i++)
        {
            collection.add(decoder.decode(buffer));
        }
        return collection;
    }

    public static <B extends ByteBuf, E, C extends Collection<E>> void writeClampedCollection(StreamEncoder<? super B, E> encoder, B buffer, C collection, int min, int max)
    {
        int size = collection.size();
        writeClampedVarInt(buffer, size, min, max);
        collection.forEach(o -> encoder.encode(buffer, o));
    }
    //#endregion

    //#region Stream codec operations
    public static <B extends ByteBuf, T> StreamCodec.CodecOperation<B, T, Optional<T>> asOptional()
    {
        return ByteBufCodecs::optional;
    }

    public static <B extends ByteBuf, T, I extends T> StreamCodec.CodecOperation<B, T, I> classCastMap(Class<I> iClass)
    {
        return baseCodec -> baseCodec.map(t -> LimaCoreUtil.castOrThrow(iClass, t), Function.identity());
    }

    public static <B extends ByteBuf, E, C extends Collection<E>> StreamCodec.CodecOperation<B, E, C> asClampedCollection(IntFunction<? extends C> factory, int minInclusive, int maxInclusive)
    {
        return elementCodec -> clampedCollection(elementCodec, factory, minInclusive, maxInclusive);
    }

    public static <B extends ByteBuf, E> StreamCodec.CodecOperation<B, E, List<E>> asClampedList(int minInclusive, int maxInclusive)
    {
        return elementCodec -> clampedList(elementCodec, minInclusive, maxInclusive);
    }
    //#endregion

    //#region Stream codec factories
    public static StreamCodec<RegistryFriendlyByteBuf, List<SizedIngredient>> sizedIngredients(int minInclusive, int maxInclusive)
    {
        return SizedIngredient.STREAM_CODEC.apply(asClampedList(minInclusive, maxInclusive));
    }

    public static StreamCodec<RegistryFriendlyByteBuf, List<SizedIngredient>> sizedIngredients(int maxIngredients)
    {
        return sizedIngredients(1, maxIngredients);
    }

    public static StreamCodec<RegistryFriendlyByteBuf, List<SizedFluidIngredient>> sizedFluidIngredients(int minInclusive, int maxInclusive)
    {
        return SizedFluidIngredient.STREAM_CODEC.apply(asClampedList(minInclusive, maxInclusive));
    }

    public static StreamCodec<RegistryFriendlyByteBuf, List<FluidStack>> fluidResults(int minInclusive, int maxInclusive)
    {
        return FluidStack.STREAM_CODEC.apply(asClampedList(minInclusive, maxInclusive));
    }

    public static <T, U extends T> StreamCodec<RegistryFriendlyByteBuf, U> classCastRegistryStreamCodec(ResourceKey<? extends Registry<T>> registryKey, Class<U> valueClass)
    {
        return ByteBufCodecs.registry(registryKey).apply(classCastMap(valueClass));
    }

    public static <T extends Entity> StreamCodec<ByteBuf, Optional<T>> remoteEntity(Class<T> entityClass)
    {
        return new StreamCodec<>()
        {
            @Override
            public Optional<T> decode(ByteBuf buffer)
            {
                int eid = VarInt.read(buffer);
                return Optional.ofNullable(LimaCoreClientUtil.getClientEntity(eid, entityClass));
            }

            @Override
            public void encode(ByteBuf buffer, Optional<T> value)
            {
                int eid = value.filter(e -> !e.isRemoved()).map(Entity::getId).orElse(-1);
                VarInt.write(buffer, eid);
            }
        };
    }

    public static StreamCodec<ByteBuf, Integer> varIntRange(int min, int max)
    {
        return StreamCodec.of((net, n) -> writeClampedVarInt(net, n, min, max), net -> readClampedVarInt(net, min, max));
    }

    public static StreamCodec<ByteBuf, Float> floatRange(float min, float max)
    {
        return StreamCodec.of((net, n) -> writeClampedFloat(net, n, min, max), net -> readClampedFloat(net, min, max));
    }

    public static StreamCodec<ByteBuf, Double> doubleRange(double min, double max)
    {
        return StreamCodec.of((net, n) -> writeClampedDouble(net, n, min, max), net -> readClampedDouble(net, min, max));
    }

    public static <B extends ByteBuf, E, C extends Collection<E>> StreamCodec<B, C> clampedCollection(StreamCodec<? super B, E> elementCodec, IntFunction<? extends C> factory, int min, int max)
    {
        return StreamCodec.of((net, collection) -> writeClampedCollection(elementCodec, net, collection, min, max), net -> readClampedCollection(elementCodec, net, factory, min, max));
    }

    public static <B extends ByteBuf, E> StreamCodec<B, List<E>> clampedList(StreamCodec<? super B, E> elementCodec, int min, int max)
    {
        return clampedCollection(elementCodec, ObjectArrayList::new, min, max);
    }

    public static <B extends ByteBuf, E> StreamCodec<B, ObjectSet<E>> objectSet(StreamCodec<? super B, E> elementCodec)
    {
        return ByteBufCodecs.collection(ObjectOpenHashSet::new, elementCodec);
    }

    public static <B extends ByteBuf, K, V> StreamCodec<B, Object2ObjectMap<K, V>> object2ObjectMap(StreamCodec<? super B, K> keyCodec, StreamCodec<? super B, V> valueCodec)
    {
        return ByteBufCodecs.map(Object2ObjectOpenHashMap::new, keyCodec, valueCodec);
    }

    public static <B extends ByteBuf, K> StreamCodec<B, Object2IntMap<K>> object2IntMap(StreamCodec<? super B, K> keyCodec, StreamCodec<? super B, Integer> valueCodec)
    {
        return ByteBufCodecs.map(Object2IntOpenHashMap::new, keyCodec, valueCodec);
    }

    public static <B extends ByteBuf, K> StreamCodec<B, Object2IntMap<K>> object2IntMap(StreamCodec<? super B, K> keyCodec)
    {
        return object2IntMap(keyCodec, ByteBufCodecs.VAR_INT);
    }
    //#endregion
}