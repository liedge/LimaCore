package liedge.limacore.network;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.util.LimaCoreUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;

public final class LimaStreamCodecs
{
    private LimaStreamCodecs() {}

    // Common registries
    public static final StreamCodec<RegistryFriendlyByteBuf, Item> ITEM_DIRECT = ByteBufCodecs.registry(Registries.ITEM);

    // Extra codecs
    public static final StreamCodec<ByteBuf, Integer> NON_NEGATIVE_VAR_INT = varIntRange(0, Integer.MAX_VALUE);
    public static final StreamCodec<ByteBuf, Integer> POSITIVE_VAR_INT = varIntRange(1, Integer.MAX_VALUE);

    public static final StreamCodec<ByteBuf, Optional<Entity>> REMOTE_ENTITY = new StreamCodec<>()
    {
        @Override
        public Optional<Entity> decode(ByteBuf net)
        {
            int eid = VarInt.read(net);
            return Optional.ofNullable(LimaCoreClientUtil.getClientEntity(eid));
        }

        @Override
        public void encode(ByteBuf net, Optional<Entity> value)
        {
            int eid = value.map(e -> !e.isRemoved() ? e.getId() : -1).orElse(-1);
            VarInt.write(net, eid);
        }
    };

    public static StreamCodec<ByteBuf, Vec3> VEC3D = StreamCodec.of((net, vec) -> net.writeDouble(vec.x).writeDouble(vec.y).writeDouble(vec.z), net -> new Vec3(net.readDouble(), net.readDouble(), net.readDouble()));

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

    public static <B extends ByteBuf, T> Optional<T> readOptional(StreamDecoder<? super B, T> decoder, B buffer)
    {
        return buffer.readBoolean() ? Optional.of(decoder.decode(buffer)) : Optional.empty();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <B extends ByteBuf, T> void writeOptional(StreamEncoder<? super B, T> encoder, B buffer, Optional<T> value)
    {
        boolean present = value.isPresent();
        buffer.writeBoolean(present);
        if (present) encoder.encode(buffer, value.get());
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
        return LimaStreamCodecs::optionalValue;
    }

    public static <B extends ByteBuf, T, I extends T> StreamCodec.CodecOperation<B, T, I> classCastMap(Class<I> iClass)
    {
        return baseCodec -> baseCodec.map(t -> LimaCoreUtil.castOrThrow(iClass, t), Function.identity());
    }

    public static <B extends ByteBuf, E, C extends Collection<E>> StreamCodec.CodecOperation<B, E, C> asClampedCollection(IntFunction<? extends C> factory, int minInclusive, int maxInclusive)
    {
        return elementCodec -> clampedCollection(elementCodec, factory, minInclusive, maxInclusive);
    }
    //#endregion

    //#region Stream codec factories
    public static StreamCodec<RegistryFriendlyByteBuf, NonNullList<Ingredient>> ingredientsStreamCodec(int minInclusive, int maxInclusive)
    {
        return Ingredient.CONTENTS_STREAM_CODEC.apply(asClampedCollection(NonNullList::createWithCapacity, minInclusive, maxInclusive));
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
        return StreamCodec.of((net, col) -> writeClampedCollection(elementCodec, net, col, min, max), net -> readClampedCollection(elementCodec, net, factory, min, max));
    }

    public static <B extends ByteBuf, T> StreamCodec<B, Optional<T>> optionalValue(StreamCodec<? super B, T> baseCodec)
    {
        return StreamCodec.of((net, o) -> writeOptional(baseCodec, net, o), net -> readOptional(baseCodec, net));
    }
    //#endregion
}