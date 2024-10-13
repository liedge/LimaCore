package liedge.limacore.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.limacore.util.LimaStreamsUtil;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public final class LimaEnumCodec<A extends Enum<A> & StringRepresentable> implements Codec<A>
{
    public static <E extends Enum<E> & StringRepresentable> LimaEnumCodec<E> createStrict(Class<E> enumClass)
    {
        return new LimaEnumCodec<>(enumClass, null);
    }

    public static <E extends Enum<E> & StringRepresentable> LimaEnumCodec<E> createLenient(Class<E> enumClass, @NotNull E defaultValue)
    {
        return new LimaEnumCodec<>(enumClass, Objects.requireNonNull(defaultValue, "Lenient enum codec must have a non-null default value"));
    }

    private final String name;
    private final @Nullable A defaultValue;
    private final A[] values;
    private final Map<String, A> nameLookup;
    private final Codec<A> baseCodec;

    private LimaEnumCodec(Class<A> enumClass, @Nullable A defaultValue)
    {
        this.name = "LimaEnumCodec[" + enumClass.getSimpleName() + "]";
        this.defaultValue = defaultValue;
        this.values = LimaCollectionsUtil.checkedEnumConstants(enumClass);
        this.nameLookup = Stream.of(values).collect(LimaStreamsUtil.toUnmodifiableObject2ObjectMap(StringRepresentable::getSerializedName, Function.identity()));
        this.baseCodec = ExtraCodecs.orCompressed(
                Codec.stringResolver(StringRepresentable::getSerializedName, this::byNameInternal),
                ExtraCodecs.idResolverCodec(Enum::ordinal, this::byOrdinalInternal, -1));
    }

    private @Nullable A byNameInternal(String name)
    {
        return nameLookup.getOrDefault(name, defaultValue);
    }

    private @Nullable A byOrdinalInternal(int ordinal)
    {
        if (ordinal >= 0 && ordinal < values.length)
        {
            return values[ordinal];
        }
        else
        {
            return defaultValue;
        }
    }

    public A byName(String name)
    {
        return Objects.requireNonNull(byNameInternal(name), this + " doesn't support default values");
    }

    public A byName(String name, A fallback)
    {
        return Objects.requireNonNullElse(byNameInternal(name), fallback);
    }

    public A byOrdinal(int ordinal)
    {
        return Objects.requireNonNull(byOrdinalInternal(ordinal), this + " doesn't support default values");
    }

    public A byOrdinal(int ordinal, A fallback)
    {
        return Objects.requireNonNullElse(byOrdinalInternal(ordinal), fallback);
    }

    @Override
    public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input)
    {
        return baseCodec.decode(ops, input);
    }

    @Override
    public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix)
    {
        return baseCodec.encode(input, ops, prefix);
    }

    @Override
    public String toString()
    {
        return name;
    }
}