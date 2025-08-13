package liedge.limacore.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
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
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static liedge.limacore.util.LimaMathUtil.toDeg;
import static liedge.limacore.util.LimaMathUtil.toRad;

public final class LimaCoreCodecs
{
    private static final Logger LOGGER = LogUtils.getLogger();

    private LimaCoreCodecs() {}

    // Map codec common keys
    public static final String INGREDIENTS_KEY = "ingredients";
    public static final String FLUID_INGREDIENTS_KEY = "fluid_ingredients";
    public static final String FLUID_RESULTS_KEY = "fluid_results";

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

    public static final MapCodec<ItemStack> ITEM_STACK_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.ITEM_NON_AIR_CODEC.fieldOf("id").forGetter(ItemStack::getItemHolder),
            Codec.intRange(1, 99).fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemStack::getComponentsPatch))
            .apply(instance, ItemStack::new));

    public static Codec<Vector3f> AXIS_VECTOR = Codec.withAlternative(ExtraCodecs.VECTOR3F, Direction.Axis.CODEC, LimaMathUtil::unitVecForAxis);

    public static final MapCodec<AxisAngle4f> UNIT_AXIS_ANGLE4F = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DEG_TO_RAD_FLOAT.fieldOf("angle").forGetter(o -> o.angle),
            AXIS_VECTOR.fieldOf("axis").forGetter(o -> new Vector3f(o.x, o.y, o.z))).apply(instance, AxisAngle4f::new));

    public static final MapCodec<Quaternionf> UNIT_QUATERNION = UNIT_AXIS_ANGLE4F.xmap(Quaternionf::new, AxisAngle4f::new);
    public static final MapCodec<List<SizedIngredient>> ITEM_INGREDIENTS_UNIT = EmptyFieldMapCodec.emptyListField(INGREDIENTS_KEY);
    public static final MapCodec<List<SizedFluidIngredient>> FLUID_INGREDIENTS_UNIT = EmptyFieldMapCodec.emptyListField(FLUID_INGREDIENTS_KEY);
    public static final MapCodec<List<FluidStack>> FLUID_RESULTS_UNIT = EmptyFieldMapCodec.emptyListField(FLUID_RESULTS_KEY);

    public static <N extends Number & Comparable<N>> Codec<N> openStartNumberRange(Codec<N> baseCodec, N minExclusive, N maxInclusive)
    {
        return baseCodec.validate(num ->
        {
            if (num.compareTo(minExclusive) > 0 && num.compareTo(maxInclusive) <= 0)
                return DataResult.success(num);
            else
                return DataResult.error(() -> String.format("Value %s outside of valid range (%s,%s]", num, minExclusive, maxInclusive));
        });
    }

    public static <N extends Number & Comparable<N>> Codec<N> openEndNumberRange(Codec<N> baseCodec, N minInclusive, N maxExclusive)
    {
        return baseCodec.validate(num ->
        {
            if (num.compareTo(minInclusive) >= 0 && num.compareTo(maxExclusive) < 0)
                return DataResult.success(num);
            else
                return DataResult.error(() -> String.format("Value %s outside of valid range [%s,%s)", num, minInclusive, maxExclusive));
        });
    }

    public static Codec<Float> floatOpenStartRange(float minExclusive, float maxInclusive)
    {
        return openStartNumberRange(Codec.FLOAT, minExclusive, maxInclusive);
    }

    public static <E extends Enum<E>> Codec<Set<E>> enumSetCodec(Codec<E> enumElementCodec)
    {
        return enumElementCodec.listOf().xmap(list -> list.isEmpty() ? Set.of() : ImmutableSet.copyOf(EnumSet.copyOf(list)), List::copyOf);
    }

    public static <E> Codec<ObjectSet<E>> objectSetCodec(Codec<E> elementCodec)
    {
        return elementCodec.listOf().xmap(list -> ObjectSets.unmodifiable(new ObjectLinkedOpenHashSet<>(list)), List::copyOf);
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

    public static <T, A, F extends A> Codec<A> flatDispatchCodec(Codec<T> typeCodec, Class<F> flatClass, Codec<F> flatCodec, Function<? super A, ? extends T> typeFunction, Function<? super T, MapCodec<? extends A>> typeCodecFunction)
    {
        return Codec.xor(flatCodec, typeCodec.dispatch(typeFunction, typeCodecFunction)).xmap(Either::unwrap, value -> flatClass.isInstance(value) ? Either.left(flatClass.cast(value)) : Either.right(value));
    }

    public static <E> MapCodec<List<E>> smartSizedListField(Codec<E> elementCodec, String fieldName, int minInclusive, int maxInclusive)
    {
        Preconditions.checkArgument(minInclusive >= 0, "Minimum size must be non-negative.");
        Preconditions.checkArgument(maxInclusive >= minInclusive, "Maximum size must be greater than or equal to minimum size.");

        Codec<List<E>> listCodec = elementCodec.listOf(minInclusive, maxInclusive);
        return minInclusive == 0 ? listCodec.optionalFieldOf(fieldName, List.of()) : listCodec.fieldOf(fieldName);
    }

    public static MapCodec<List<SizedIngredient>> sizedIngredients(int minInclusive, int maxInclusive)
    {
        return smartSizedListField(SizedIngredient.FLAT_CODEC, INGREDIENTS_KEY, minInclusive, maxInclusive);
    }

    public static MapCodec<List<SizedFluidIngredient>> sizedFluidIngredients(int minInclusive, int maxInclusive)
    {
        return smartSizedListField(SizedFluidIngredient.FLAT_CODEC, FLUID_INGREDIENTS_KEY, minInclusive, maxInclusive);
    }

    public static MapCodec<List<FluidStack>> fluidResults(int minInclusive, int maxInclusive)
    {
        return smartSizedListField(FluidStack.CODEC, FLUID_RESULTS_KEY, minInclusive, maxInclusive);
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