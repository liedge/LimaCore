package liedge.limacore.data;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function3;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.*;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limacore.util.LimaMathUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static liedge.limacore.util.LimaMathUtil.toDeg;
import static liedge.limacore.util.LimaMathUtil.toRad;

public final class LimaCoreCodecs
{
    private static final Logger LOGGER = LogUtils.getLogger();

    private LimaCoreCodecs() {}

    /**
     * Float codec for decoding into radians for code use and encoding into degrees for readability.
     */
    public static final Codec<Float> DEG_TO_RAD_FLOAT = new PrimitiveCodec<>()
    {
        @Override
        public <T> DataResult<Float> read(DynamicOps<T> ops, T input)
        {
            return ops.getNumberValue(input).map(n -> toRad(n.floatValue()));
        }

        @Override
        public <T> T write(DynamicOps<T> ops, Float value)
        {
            return ops.createFloat(toDeg(value));
        }

        @Override
        public String toString()
        {
            return "Degrees to Radians Float";
        }
    };

    /**
     * Hexadecimal integer codec. Encoded values will be prefixed with '#'. Decoded values
     * do not need the '#' prefix but may contain it.
     */
    public static final Codec<Integer> HEXADECIMAL_INT = new PrimitiveCodec<>()
    {
        @Override
        public <T> DataResult<Integer> read(DynamicOps<T> ops, T input)
        {
            return ops.getStringValue(input).flatMap(string -> {
                try
                {
                    return DataResult.success(LimaMathUtil.parseHexadecimal(string));
                }
                catch (NumberFormatException ex)
                {
                    return DataResult.error(() -> "Not a valid hexadecimal string: " + ex.getMessage());
                }
            });
        }

        @Override
        public <T> T write(DynamicOps<T> ops, Integer value)
        {
            return ops.createString("#" + Integer.toHexString(value));
        }

        @Override
        public String toString()
        {
            return "Hexadecimal Int";
        }
    };

    /**
     * Strict {@link Direction} codec with {@link LimaEnumCodec} convenience extensions.
     */
    public static final LimaEnumCodec<Direction> STRICT_DIRECTION = LimaEnumCodec.create(Direction.class);

    /**
     * Vector codec that scales decoded values down by 16x and encoded values up by 16x. Useful for baked models.
     */
    public static final Codec<Vector3f> VECTOR3F_16X = ExtraCodecs.VECTOR3F.xmap(vec -> vec.mul(0.0625f), vec -> vec.mul(16));

    public static Codec<Vector3f> AXIS_VECTOR = Codec.withAlternative(ExtraCodecs.VECTOR3F, Direction.Axis.CODEC, LimaMathUtil::unitVecForAxis);

    public static final MapCodec<AxisAngle4f> UNIT_AXIS_ANGLE4F = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DEG_TO_RAD_FLOAT.fieldOf("angle").forGetter(o -> o.angle),
            AXIS_VECTOR.fieldOf("axis").forGetter(o -> new Vector3f(o.x, o.y, o.z))).apply(instance, AxisAngle4f::new));

    public static final MapCodec<Quaternionf> UNIT_QUATERNION = UNIT_AXIS_ANGLE4F.xmap(Quaternionf::new, AxisAngle4f::new);

    public static <E> Codec<ObjectSet<E>> objectSetCodec(Codec<E> elementCodec)
    {
        return elementCodec.listOf().xmap(list -> ObjectSets.unmodifiable(new ObjectOpenHashSet<>(list)), List::copyOf);
    }

    public static <K> Codec<Object2IntMap<K>> object2IntMap(Codec<K> keyCodec, Codec<Integer> valueCodec)
    {
        return Codec.unboundedMap(keyCodec, valueCodec).xmap(map -> Object2IntMaps.unmodifiable(new Object2IntOpenHashMap<>(map)), Function.identity());
    }

    public static <K> Codec<Object2IntMap<K>> object2IntMap(Codec<K> keyCodec)
    {
        return object2IntMap(keyCodec, Codec.INT);
    }

    public static <R, T extends R> Codec<T> classCastRegistryCodec(Registry<R> registry, Class<T> valueClass)
    {
        return registry.byNameCodec().comapFlatMap(o -> nullableDataResult(LimaCoreUtil.castOrNull(valueClass, o), () -> "Registry object is not an instance of '" + valueClass.getSimpleName()), Function.identity());
    }

    public static <A, B1 extends A, B2 extends A> Codec<A> eitherSubclassCodec(Codec<B1> firstCodec, Codec<B2> secondCodec, Class<B1> firstClass, Class<B2> secondClass)
    {
        return Codec.xor(firstCodec, secondCodec).flatComapMap(
                either -> either.map(Function.identity(), Function.identity()),
                value -> {
                    if (firstClass.isInstance(value)) return DataResult.success(Either.left(firstClass.cast(value)));
                    else if (secondClass.isInstance(value)) return DataResult.success(Either.right(secondClass.cast(value)));
                    else return DataResult.error(() -> "Value not an instance of either " + firstClass.getName() + " or " + secondClass.getName());
                });
    }

    public static <T, A, F extends A> Codec<A> flatDispatchCodec(Codec<T> typeCodec, Class<F> flatClass, Codec<F> flatCodec, Function<? super A, ? extends T> typeFunction, Function<? super T, MapCodec<? extends A>> typeCodecFunction)
    {
        return Codec.xor(flatCodec, typeCodec.dispatch(typeFunction, typeCodecFunction)).xmap(
                either -> either.map(Function.identity(), Function.identity()),
                value -> flatClass.isInstance(value) ? Either.left(flatClass.cast(value)) : Either.right(value));
    }

    public static MapCodec<NonNullList<Ingredient>> ingredientsMapCodec(int minInclusive, int maxInclusive)
    {
        return Ingredient.CODEC_NONEMPTY.listOf(minInclusive, maxInclusive).xmap(NonNullList::copyOf, Function.identity()).fieldOf("ingredients");
    }

    public static MapCodec<List<SizedIngredient>> sizedIngredientsMapCodec(int minInclusive, int maxInclusive)
    {
        return SizedIngredient.FLAT_CODEC.listOf(minInclusive, maxInclusive).xmap(Function.identity(), Function.identity()).fieldOf("ingredients");
    }

    public static <E, A> DataResult<A> fixedListFlatMap(List<E> list, int expectedSize, Function<IntFunction<E>, ? extends A> elementAccessor)
    {
        if (list.size() == expectedSize)
        {
            try
            {
                A result = elementAccessor.apply(list::get);
                return DataResult.success(result);
            }
            catch (IndexOutOfBoundsException ignored)
            {
                return DataResult.error(() -> "Input mapping function accesses index outside valid range [0," + expectedSize + ")");
            }
        }
        else
        {
            return DataResult.error(() -> "Input is not a list of " + expectedSize + " elements.");
        }
    }

    public static <T> DataResult<T> nullableDataResult(@Nullable T value, Supplier<String> errorMessageSupplier)
    {
        return value != null ? DataResult.success(value) : DataResult.error(errorMessageSupplier);
    }

    public static <E, A> Codec<A> fixedListComapFlatMap(Codec<E> elementCodec, int size, Function<IntFunction<E>, ? extends A> to, Function<? super A, ? extends List<E>> from)
    {
        return elementCodec.listOf().comapFlatMap(rawList -> fixedListFlatMap(rawList, size, to), from);
    }

    public static <E, T> Codec<T> triComapFlatMap(Codec<E> elementCodec, Function3<E, E, E, ? extends T> to, Function<? super T, ? extends List<E>> from)
    {
        return fixedListComapFlatMap(elementCodec, 3, list -> to.apply(list.apply(0), list.apply(1), list.apply(2)), from);
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

    public static <A, U> U lenientEncode(Codec<A> codec, DynamicOps<U> ops, A input)
    {
        return codec.encodeStart(ops, input).resultOrPartial(msg -> LOGGER.warn("Codec {} encountered errors during lenient encoding: {}", codec, msg)).orElseThrow(() -> new IllegalStateException("Encoding error."));
    }

    public static <A, U> A strictDecode(Codec<A> codec, DynamicOps<U> ops, U input)
    {
        return codec.decode(ops, input).getOrThrow(msg -> {
            LOGGER.error("Codec {} failed strict decoding: {}", codec, msg);
            throw new IllegalStateException("Decoding error.");
        }).getFirst();
    }

    public static <A, U> A lenientDecode(Codec<A> codec, DynamicOps<U> ops, U input)
    {
        return codec.decode(ops, input)
                .resultOrPartial(msg -> LOGGER.warn("Codec {} encountered errors during lenient decoding: {}", codec, msg))
                .orElseThrow(() -> new IllegalStateException("Decoding error."))
                .getFirst();
    }
    //#endregion
}