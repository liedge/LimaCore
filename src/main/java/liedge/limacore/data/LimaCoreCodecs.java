package liedge.limacore.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.*;
import it.unimi.dsi.fastutil.objects.*;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.util.LimaCoreObjects;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class LimaCoreCodecs
{
    private static final Logger LOGGER = LogUtils.getLogger();

    private LimaCoreCodecs() {}

    private static DataResult<Integer> parseHexadecimal(String rawString)
    {
        try
        {
            return DataResult.success(LimaCoreMath.parseHexadecimal(rawString));
        }
        catch (NumberFormatException ignored)
        {
            return DataResult.error(() -> rawString + " is not a hexadecimal number.");
        }
    }

    private static DataResult<Float> parseAngle(String rawString)
    {
        String input = rawString.trim();
        if (!input.endsWith("deg")) return DataResult.error(() -> "Invalid angle input: " + input);

        String numString = input.substring(0, input.length() - 3);
        try
        {
            float value = Float.parseFloat(numString);
            return DataResult.success(LimaCoreMath.toRad(value));
        }
        catch (NumberFormatException ignored)
        {
            return DataResult.error(() -> rawString + " is not a decimal number.");
        }
    }

    /**
     * Hexadecimal integer codec. Encoded values will be prefixed with '#'. Decoded values
     * do not need the '#' prefix but may contain it.
     */
    public static final Codec<Integer> HEXADECIMAL_INT = Codec.STRING.comapFlatMap(LimaCoreCodecs::parseHexadecimal, num -> "#" + Integer.toHexString(num));

    /**
     * Float codec for radians. Can optionally fix degree inputs into radians.
     */
    public static final Codec<Float> ANGLE_FLOAT = Codec.either(Codec.FLOAT, Codec.STRING).comapFlatMap(
            either -> either.map(DataResult::success, LimaCoreCodecs::parseAngle),
            Either::left);

    /**
     * Strict {@link Direction} codec with {@link LimaEnumCodec} convenience extensions.
     */
    public static final LimaEnumCodec<Direction> STRICT_DIRECTION = LimaEnumCodec.create(Direction.class);

    public static final Codec<Vector3f> MODEL_VECTOR = decodeOnly(ExtraCodecs.VECTOR3F.map(vec -> {
        Vector3f out = new Vector3f(vec);
        return out.mul(0.0625f);
    }));

    public static <A> Codec<A> decodeOnly(Decoder<A> decoder)
    {
        String name = decoder + "[readOnly]";
        Encoder<A> encoder = Encoder.error(name + " is a decode-only codec.");
        return Codec.of(encoder, decoder, name);
    }

    public static <N extends Number & Comparable<N>> Codec<N> openStartNumberRange(Codec<N> baseCodec, N minExclusive, N maxInclusive)
    {
        return baseCodec.validate(num ->
        {
            if (LimaCoreObjects.inRangeOpenStart(num, minExclusive, maxInclusive))
                return DataResult.success(num);
            else
                return DataResult.error(() -> String.format("Value %s outside of valid range (%s,%s]", num, minExclusive, maxInclusive));
        });
    }

    public static <N extends Number & Comparable<N>> Codec<N> openEndNumberRange(Codec<N> baseCodec, N minInclusive, N maxExclusive)
    {
        return baseCodec.validate(num ->
        {
            if (LimaCoreObjects.inRangeOpenEnd(num, minInclusive, maxExclusive))
                return DataResult.success(num);
            else
                return DataResult.error(() -> String.format("Value %s outside of valid range [%s,%s)", num, minInclusive, maxExclusive));
        });
    }

    public static <N extends Number & Comparable<N>> Codec<N> openNumberRange(Codec<N> baseCodec, N minExclusive, N maxExclusive)
    {
        return baseCodec.validate(num ->
        {
            if (LimaCoreObjects.inRangeOpen(num, minExclusive, maxExclusive))
                return DataResult.success(num);
            else
                return DataResult.error(() -> String.format("Value %s outside of valid range (%s,%s)", num, minExclusive, maxExclusive));
        });
    }

    public static Codec<Float> floatOpenStartRange(float minExclusive, float maxInclusive)
    {
        return openStartNumberRange(Codec.FLOAT, minExclusive, maxInclusive);
    }

    public static Codec<Float> floatOpenEndRange(float minInclusive, float maxExclusive)
    {
        return openEndNumberRange(Codec.FLOAT, minInclusive, maxExclusive);
    }

    public static Codec<Float> floatOpenRange(float minExclusive, float maxExclusive)
    {
        return openNumberRange(Codec.FLOAT, minExclusive, maxExclusive);
    }

    public static <E> MapCodec<List<E>> singleOrPluralBoundedList(Codec<E> elementCodec, String singularFieldName, int minInclusive, int maxInclusive)
    {
        MapCodec<E> singular = elementCodec.fieldOf(singularFieldName);
        MapCodec<List<E>> plural = autoOptionalListField(elementCodec, singularFieldName + 's', minInclusive, maxInclusive);

        return Codec.mapEither(singular, plural).xmap(
                either -> either.map(List::of, Function.identity()),
                list -> list.size() == 1 ? Either.left(list.getFirst()) : Either.right(list));
    }

    public static <E> MapCodec<List<E>> singleOrPluralNonEmpty(Codec<E> elementCodec, String singularFieldName)
    {
        return singleOrPluralBoundedList(elementCodec, singularFieldName, 1, Integer.MAX_VALUE);
    }

    public static <E extends Enum<E>> Codec<Set<E>> enumSetCodec(Codec<E> enumElementCodec)
    {
        return enumElementCodec.listOf().xmap(list -> list.isEmpty() ? Set.of() : ImmutableSet.copyOf(EnumSet.copyOf(list)), List::copyOf);
    }

    public static <E> Codec<ObjectSet<E>> objectSetCodec(Codec<E> elementCodec)
    {
        return elementCodec.listOf().xmap(list -> ObjectSets.unmodifiable(new ObjectLinkedOpenHashSet<>(list)), List::copyOf);
    }

    public static <K> Codec<Object2IntMap<K>> object2IntMap(Codec<K> keyCodec, Codec<Integer> valueCodec, Function<Map<K, Integer>, ? extends Object2IntMap<K>> wrapper)
    {
        return Codec.unboundedMap(keyCodec, valueCodec).xmap(boxedMap -> Object2IntMaps.unmodifiable(wrapper.apply(boxedMap)), Function.identity());
    }

    public static <K> Codec<Object2IntMap<K>> object2IntHashMap(Codec<K> keyCodec, Codec<Integer> valueCodec)
    {
        return object2IntMap(keyCodec, valueCodec, Object2IntOpenHashMap::new);
    }

    public static <K> Codec<Object2IntMap<K>> object2IntLinkedHashMap(Codec<K> keyCodec, Codec<Integer> valueCodec)
    {
        return object2IntMap(keyCodec, valueCodec, Object2IntLinkedOpenHashMap::new);
    }

    public static <R, T extends R> Codec<T> classCastRegistryCodec(Registry<R> registry, Class<T> valueClass)
    {
        return registry.byNameCodec().comapFlatMap(o -> nullableDataResult(LimaCoreObjects.tryCast(valueClass, o), () -> "Registry object is not an instance of '" + valueClass.getSimpleName()), Function.identity());
    }

    public static <A, L extends A, R extends A> DataResult<Either<L, R>> xorSubclassDataResult(A value, Class<L> leftClass, Class<R> rightClass)
    {
        if (leftClass.isInstance(value)) return DataResult.success(Either.left(leftClass.cast(value)));
        else if (rightClass.isInstance(value)) return DataResult.success(Either.right(rightClass.cast(value)));
        else return DataResult.error(() -> "Value is not an instance of either " + leftClass.getName() + " or " + rightClass.getName());
    }

    public static <A, L extends A, R extends A> Codec<A> xorSubclassCodec(Codec<L> leftCodec, Codec<R> rightCodec, Class<L> leftClass, Class<R> rightClass)
    {
        return Codec.xor(leftCodec, rightCodec).flatComapMap(Either::unwrap, value -> xorSubclassDataResult(value, leftClass, rightClass));
    }

    public static <A, L extends A, R extends A> MapCodec<A> xorSubclassMapCodec(MapCodec<L> leftCodec, MapCodec<R> rightCodec, Class<L> leftClass, Class<R> rightClass)
    {
        return flatComapMapMapCodec(NeoForgeExtraCodecs.xor(leftCodec, rightCodec), value -> xorSubclassDataResult(value, leftClass, rightClass), Either::unwrap);
    }

    private static <A, F extends A> Either<F, A> inlinedSubclassEither(Class<F> inlineClass, A value)
    {
        return inlineClass.isInstance(value) ? Either.left(inlineClass.cast(value)) : Either.right(value);
    }

    public static <T, A, F extends A> Codec<A> dispatchWithInline(Codec<T> typeCodec, String typeKey, Class<F> inlineClass, Codec<F> inlineCodec, Function<? super A, ? extends T> typeGetter, Function<? super T, MapCodec<? extends A>> codecGetter)
    {
        return Codec.xor(inlineCodec, typeCodec.dispatch(typeKey, typeGetter, codecGetter)).xmap(Either::unwrap, value -> inlinedSubclassEither(inlineClass, value));
    }

    public static <T, A, F extends A> Codec<A> dispatchWithInline(Codec<T> typeCodec, Class<F> inlineClass, Codec<F> inlineCodec, Function<? super A, ? extends T> typeGetter, Function<? super T, MapCodec<? extends A>> codecGetter)
    {
        return dispatchWithInline(typeCodec, "type", inlineClass, inlineCodec, typeGetter, codecGetter);
    }

    public static <T, A, F extends A> MapCodec<A> dispatchMapWithInline(Codec<T> typeCodec, String typeKey, Class<F> inlineClass, MapCodec<F> inlineCodec, Function<? super A, ? extends T> typeGetter, Function<? super T, MapCodec<? extends A>> codecGetter)
    {
        return NeoForgeExtraCodecs.xor(inlineCodec, typeCodec.dispatchMap(typeKey, typeGetter, codecGetter)).xmap(Either::unwrap, value -> inlineClass.isInstance(value) ? Either.left(inlineClass.cast(value)) : Either.right(value));
    }

    public static <T, A, F extends A> MapCodec<A> dispatchMapWithInline(Codec<T> typeCodec, Class<F> inlineClass, MapCodec<F> inlineCodec, Function<? super A, ? extends T> typeGetter, Function<? super T, MapCodec<? extends A>> codecGetter)
    {
        return dispatchMapWithInline(typeCodec, "type", inlineClass, inlineCodec, typeGetter, codecGetter);
    }

    public static <E> MapCodec<List<E>> autoOptionalListField(Codec<E> elementCodec, String fieldName, int minInclusive, int maxInclusive)
    {
        Preconditions.checkArgument(minInclusive >= 0, "Minimum size must be non-negative.");
        Preconditions.checkArgument(maxInclusive >= minInclusive, "Maximum size must be greater than or equal to minimum size.");

        Codec<List<E>> listCodec = elementCodec.listOf(minInclusive, maxInclusive);
        return minInclusive == 0 ? listCodec.optionalFieldOf(fieldName, List.of()) : listCodec.fieldOf(fieldName);
    }

    public static <T> DataResult<T> nullableDataResult(@Nullable T value, Supplier<String> errorMessageSupplier)
    {
        return value != null ? DataResult.success(value) : DataResult.error(errorMessageSupplier);
    }

    public static <A, S> MapCodec<S> comapFlatMapMapCodec(MapCodec<A> baseCodec, Function<? super S, ? extends A> to, Function<? super A, ? extends DataResult<? extends S>> from)
    {
        return MapCodec.of(baseCodec.comap(to), baseCodec.flatMap(from));
    }

    public static <A, S> MapCodec<S> flatComapMapMapCodec(MapCodec<A> baseCodec, Function<? super S, ? extends DataResult<? extends A>> to, Function<? super A, ? extends S> from)
    {
        return MapCodec.of(baseCodec.flatComap(to), baseCodec.map(from));
    }

    // #region Encoding/Decoding utilities
    public static <A, U> U strictEncode(Codec<A> codec, DynamicOps<U> ops, A input)
    {
        return codec.encodeStart(ops, input).getOrThrow(msg -> {
            LOGGER.error("Codec {} failed strict encoding: {}", codec, msg);
            throw new IllegalStateException("Encoding error.");
        });
    }

    public static <A, U> @Nullable U tryEncode(Codec<A> codec, DynamicOps<U> ops, A input)
    {
        return partialEncode(codec, ops, input).orElse(null);
    }

    public static <A, U> void tryEncodeTo(Codec<A> codec, DynamicOps<U> ops, A input, Consumer<? super U> consumer)
    {
        partialEncode(codec, ops, input).ifPresent(consumer);
    }

    public static <A, U> A strictDecode(Codec<A> codec, DynamicOps<U> ops, U input)
    {
        return codec.decode(ops, input).getOrThrow(msg -> {
            LOGGER.error("Codec {} failed strict decoding: {}", codec, msg);
            throw new IllegalStateException("Decoding error.");
        }).getFirst();
    }

    public static <A, U> @Nullable A tryDecode(Codec<A> codec, DynamicOps<U> ops, U input)
    {
        return partialDecode(codec, ops, input).orElse(null);
    }

    public static <A, U> A tryDecode(Codec<A> codec, DynamicOps<U> ops, U input, A fallback)
    {
        return partialDecode(codec, ops, input).orElse(fallback);
    }

    public static <A, U> @Nullable A tryFlatDecode(Codec<Optional<A>> codec, DynamicOps<U> ops, U input)
    {
        return partialDecode(codec, ops, input).flatMap(Function.identity()).orElse(null);
    }

    public static <A, U> A tryFlatDecode(Codec<Optional<A>> codec, DynamicOps<U> ops, U input, A fallback)
    {
        return partialDecode(codec, ops, input).flatMap(Function.identity()).orElse(fallback);
    }

    private static <A, U> Optional<U> partialEncode(Codec<A> codec, DynamicOps<U> ops, A input)
    {
        return codec.encodeStart(ops, input).resultOrPartial(msg -> LOGGER.warn("Codec {} encountered errors during encoding: {}", codec, msg));
    }

    private static <A, U> Optional<A> partialDecode(Codec<A> codec, DynamicOps<U> ops, U input)
    {
        return codec.parse(ops, input).resultOrPartial(msg -> LOGGER.warn("Codec {} encountered errors during decoding: {}", codec, msg));
    }
    //#endregion
}