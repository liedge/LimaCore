package liedge.limacore.data;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.limacore.util.LimaStreamsUtil;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class LimaEnumCodec<A extends Enum<A> & StringRepresentable> implements Codec<A>
{
    public static <E extends Enum<E> & StringRepresentable> LimaEnumCodec<E> create(Class<E> enumClass, List<E> validValues)
    {
        return new LimaEnumCodec<>(enumClass, validValues);
    }

    public static <E extends Enum<E> & StringRepresentable> LimaEnumCodec<E> create(Class<E> enumClass)
    {
        return new LimaEnumCodec<>(enumClass, List.of(LimaCollectionsUtil.checkedEnumConstants(enumClass)));
    }

    private final String name;
    private final Map<String, A> nameLookup;
    private final String validValueString;
    private final Codec<A> baseCodec;

    private LimaEnumCodec(Class<A> enumClass, Collection<A> values)
    {
        this.name = "LimaEnumCodec[" + enumClass.getSimpleName() + "]";
        this.nameLookup = values.stream().collect(LimaStreamsUtil.toUnmodifiableObject2ObjectMap(StringRepresentable::getSerializedName, Function.identity()));
        this.validValueString = values.stream().map(StringRepresentable::getSerializedName).collect(Collectors.joining(","));
        this.baseCodec = Codec.STRING
                .flatXmap(sn -> nameLookup.containsKey(sn) ? DataResult.success(nameLookup.get(sn)) : DataResult.error(() -> "Unknown or disallowed element name '" + sn + "', allowed names: " + validValueString),
                        a -> nameLookup.containsKey(a.getSerializedName()) ? DataResult.success(a.getSerializedName()) : DataResult.error(() -> "Element not allowed for serialization, allowed elements: " + validValueString));
    }

    public @Nullable A byName(String name)
    {
        return nameLookup.get(name);
    }

    public A byNameOrThrow(String name)
    {
        return Objects.requireNonNull(byName(name), "Unknown element name '" + name + "' in " + name);
    }

    public A byNameOrElse(String name, A fallback)
    {
        return Objects.requireNonNullElse(byName(name), fallback);
    }

    public Codec<Set<A>> setOf()
    {
        return listOf().xmap(list -> list.isEmpty() ? Set.of() : ImmutableSet.copyOf(EnumSet.copyOf(list)), List::copyOf);
    }

    public <B, F extends B> Codec<B> flatDispatch(Class<F> flatClass, Codec<F> flatCodec, Function<? super B, ? extends A> typeGetter, Function<? super A, MapCodec<? extends B>> codecGetter)
    {
        return LimaCoreCodecs.flatDispatchCodec(this, flatClass, flatCodec, typeGetter, codecGetter);
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