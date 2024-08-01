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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

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
    public static final StreamCodec<ByteBuf, Integer> NON_NEGATIVE_VAR_INT = minimumInt(0);
    public static final StreamCodec<ByteBuf, Integer> POSITIVE_VAR_INT = minimumInt(1);

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

    public static StreamCodec<RegistryFriendlyByteBuf, NonNullList<Ingredient>> ingredientsStreamCodec(int minInclusive, int maxInclusive)
    {
        return Ingredient.CONTENTS_STREAM_CODEC.apply(clampedCollection(NonNullList::createWithCapacity, minInclusive, maxInclusive));
    }

    //#region Value encode/decode helpers
    public static int readMinVarInt(ByteBuf net, int min)
    {
        int value = VarInt.read(net);
        if (value < min) throw new DecoderException("Tried decoding value below minimum: " + min);
        return value;
    }

    public static void writeMinVarInt(ByteBuf net, int value, int min)
    {
        if (value < min) throw new EncoderException("Tried encoding value below minimum: " + min);
        VarInt.write(net, value);
    }

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

    public static <B extends ByteBuf, E, C extends Collection<E>> StreamCodec.CodecOperation<B, E, C> clampedCollection(IntFunction<C> factory, int minInclusive, int maxInclusive)
    {
        return elementCodec -> clampedCollection(elementCodec, factory, minInclusive, maxInclusive);
    }
    //#endregion

    //#region Stream codec factories
    public static StreamCodec<ByteBuf, Integer> minimumInt(int min)
    {
        return new StreamCodec<>()
        {
            @Override
            public Integer decode(ByteBuf net)
            {
                return readMinVarInt(net, min);
            }

            @Override
            public void encode(ByteBuf net, Integer value)
            {
                writeMinVarInt(net, value, min);
            }
        };
    }

    public static <B extends ByteBuf, T> StreamCodec<B, Optional<T>> optionalValue(StreamCodec<? super B, T> baseCodec)
    {
        return new StreamCodec<>()
        {
            @Override
            public Optional<T> decode(B net)
            {
                return net.readBoolean() ? Optional.of(baseCodec.decode(net)) : Optional.empty();
            }

            @Override
            public void encode(B net, Optional<T> value)
            {
                boolean present = value.isPresent();
                net.writeBoolean(present);
                if (present) baseCodec.encode(net, value.get());
            }
        };
    }

    public static <B extends ByteBuf, E, C extends Collection<E>> StreamCodec<B, C> clampedCollection(StreamCodec<? super B, E> elementCodec, IntFunction<C> factory, int minInclusive, int maxInclusive)
    {
        return new StreamCodec<>()
        {
            @Override
            public C decode(B net)
            {
                int size = readClampedVarInt(net, minInclusive, maxInclusive);
                C collection = factory.apply(size);

                for (int i = 0; i < size; i++)
                {
                    collection.add(elementCodec.decode(net));
                }

                return collection;
            }

            @Override
            public void encode(B net, C value)
            {
                int size = value.size();
                writeClampedVarInt(net, size, minInclusive, maxInclusive);
                value.forEach(e -> elementCodec.encode(net, e));
            }
        };
    }
    //#endregion
}