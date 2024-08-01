package liedge.limacore.data;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.util.LimaMathUtil;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Function;

import static liedge.limacore.util.LimaMathUtil.toDeg;
import static liedge.limacore.util.LimaMathUtil.toRad;

public final class LimaCoreCodecs
{
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
     * Vector codec that scales decoded values down by 16x and encoded values up by 16x. Useful for baked models.
     */
    public static final Codec<Vector3f> VECTOR3F_16X = ExtraCodecs.VECTOR3F.xmap(vec -> vec.mul(0.0625f), vec -> vec.mul(16));

    public static Codec<Vector3f> AXIS_VECTOR = Codec.withAlternative(ExtraCodecs.VECTOR3F, Direction.Axis.CODEC, LimaMathUtil::unitVecForAxis);

    public static final MapCodec<AxisAngle4f> UNIT_AXIS_ANGLE4F = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DEG_TO_RAD_FLOAT.fieldOf("angle").forGetter(o -> o.angle),
            AXIS_VECTOR.fieldOf("axis").forGetter(o -> new Vector3f(o.x, o.y, o.z))).apply(instance, AxisAngle4f::new));

    public static final MapCodec<Quaternionf> UNIT_QUATERNION = UNIT_AXIS_ANGLE4F.xmap(Quaternionf::new, AxisAngle4f::new);

    public static <R, T extends R> Codec<T> classCastRegistryCodec(Registry<R> registry, Class<T> valueClass)
    {
        return registry.byNameCodec().comapFlatMap(o -> valueClass.isInstance(o) ? DataResult.success(valueClass.cast(o)) : DataResult.error(() -> "Registry object is not an instance of " + valueClass.getSimpleName()), Function.identity());
    }

    public static MapCodec<NonNullList<Ingredient>> ingredientsMapCodec(int minInclusive, int maxInclusive)
    {
        return Ingredient.CODEC_NONEMPTY.listOf(minInclusive, maxInclusive).xmap(NonNullList::copyOf, Function.identity()).fieldOf("ingredients");
    }

    public static <E, T> Codec<T> triComapFlatMap(Codec<E> elementCodec, Function3<E, E, E, ? extends T> to, Function<? super T, ? extends List<E>> from)
    {
        return elementCodec.listOf().comapFlatMap(rawList -> Util.fixedSize(rawList, 3).map(fixedList -> to.apply(fixedList.getFirst(), fixedList.get(1), fixedList.get(2))), from);
    }
}