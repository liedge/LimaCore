package liedge.limacore.util;

import liedge.limacore.lib.math.LimaRoundingMode;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

public final class LimaMathUtil
{
    private LimaMathUtil() {}

    // Commonly used values & random
    public static final Random RANDOM = new Random(13L);

    public static final int THOUSAND = 1000;
    public static final int MILLION = 1_000_000;
    public static final int BILLION = 1_000_000_000;

    private static final Vec3 Y_UNIT_VEC = new Vec3(0, 1, 0);

    // Formats
    public static final DecimalFormat FORMAT_PERCENTAGE = new DecimalFormat("#%");
    public static final DecimalFormat FORMAT_COMMA_INT = new DecimalFormat("#,###");
    public static final DecimalFormat FORMAT_2_ROUND_FLOOR = Util.make(new DecimalFormat("#.##"), o -> o.setRoundingMode(RoundingMode.FLOOR));

    public static int parseHexadecimal(String value) throws NumberFormatException
    {
        value = value.startsWith("#") ? value.substring(1) : value;
        return Integer.parseInt(value, 16);
    }

    public static Optional<Integer> tryParseHexadecimal(String value)
    {
        try
        {
            return Optional.of(parseHexadecimal(value));
        }
        catch (NumberFormatException ex)
        {
            return Optional.empty();
        }
    }

    public static float divideFloat(float dividend, float divisor)
    {
        return divisor != 0 ? (dividend / divisor) : 0f;
    }

    public static double divideDouble(double dividend, double divisor)
    {
        return divisor != 0 ? (dividend / divisor) : 0d;
    }

    public static float divideFloatLerp(float partialTick, float dividendStart, float dividendEnd, float divisor)
    {
        return divideFloat(Mth.lerp(partialTick, dividendStart, dividendEnd), divisor);
    }

    public static int nextIntBetweenInclusive(int min, int max)
    {
        return RANDOM.nextInt(min, max + 1);
    }

    public static boolean rollRandomChance(double chance)
    {
        return RANDOM.nextDouble() <= chance;
    }

    public static int valueOf(boolean bool)
    {
        return bool ? 1 : 0;
    }

    public static void validateOpenIndexRange(int start, int end, int size)
    {
        if (start < 0 || start >= size || end > size)
        {
            throw new IllegalArgumentException(String.format("Index range (%s,%s] out of bounds: (0,%s]", start, end, size));
        }
        if (start > end)
        {
            throw new IllegalArgumentException(String.format("Start index %s cannot be higher than open end index %s", start, end));
        }
    }

    //#region Double-to-Int rounding operations
    /**
     * Rounds a double to an int by applying either a floor or ceiling operation with a random chance based on the absolute value of the decimal portion.
     * <ul>
     *     <li>Decimal ABS values < 0.5 are biased towards zero.</li>
     *     <li>Decimal ABS values > 0.5 are biased towards positive/negative infinity depending on the number's sign.</li>
     * </ul>
     * For example, -4.8 has an 80% to round towards -5 and 20% to go towards -4.
     * @param value The double to be rounded.
     * @return The whole number after rounding.
     */
    public static int roundRandomly(double value)
    {
        // Return early if value is an integer
        int base = (int) value;
        if (base == value) return base;

        double d = value - base;
        int n = valueOf(rollRandomChance(Math.abs(d))) * Mth.sign(d);

        return base + n;
    }

    public static int round(double value)
    {
        return round(value, LimaRoundingMode.NATURAL);
    }

    public static int round(double value, LimaRoundingMode mode)
    {
        return switch (mode)
        {
            case NATURAL -> Math.round((float) value);
            case FLOOR -> Mth.floor(value);
            case CEIL -> Mth.ceil(value);
            case RANDOM -> roundRandomly(value);
        };
    }

    //#endregion

    public static double triangle(double min, double max)
    {
        return min + max * (RANDOM.nextDouble() - RANDOM.nextDouble());
    }

    //#region Trig Helper functions
    public static float toDeg(float angle)
    {
        return angle * Mth.RAD_TO_DEG;
    }

    public static float toDeg(double angle)
    {
        return ((float) angle) * Mth.RAD_TO_DEG;
    }

    public static float toRad(float angle)
    {
        return angle * Mth.DEG_TO_RAD;
    }

    public static float toRad(double angle)
    {
        return ((float) angle) * Mth.DEG_TO_RAD;
    }
    //#endregion

    //#region Vector functions
    public static double vec3Length(double x, double y, double z)
    {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public static double vec3Length(Vec3 vec)
    {
        return vec3Length(vec.x(), vec.y(), vec.z());
    }

    public static double vec2Length(double x, double z)
    {
        return Math.sqrt(x * x + z * z);
    }

    public static double vec2Length(Vec3 vec)
    {
        return vec2Length(vec.x, vec.z);
    }

    public static double distanceBetween(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static Vec3 normalizedCross(Vec3 a, Vec3 b)
    {
        double x = a.y * b.z - a.z * b.y;
        double y = a.z * b.x - a.x * b.z;
        double z = a.x * b.y - a.y * b.x;

        double u = Math.sqrt(x * x + y * y + z * z);
        return u < 1e-6 ? Vec3.ZERO : new Vec3(x / u, y / u, z / u);
    }

    public static Vec3 relativePointToRotations(float xRot, float yRot, double xOffset, double yOffset, double zOffset)
    {
        Vec3 dir = createMotionVector(xRot, yRot, 1f);

        Vec3 vecZ = createMotionVector(0f, yRot, 1f);
        Vec3 vecX = vecZ.cross(Y_UNIT_VEC);

        float yRotRad = toRad(yRot);
        float sinY = Mth.sin(yRotRad);
        float cosY = Mth.cos(yRotRad);

        double nx = (dir.x * zOffset) + (vecX.x * xOffset) + (-vecZ.x * yOffset * cosY);
        double ny = (dir.y * zOffset) + (vecX.y * xOffset) + (-yOffset * sinY);
        double nz = (dir.z * zOffset) + (vecX.z * xOffset) + (-vecZ.z * yOffset * cosY);

        return new Vec3(nx, ny, nz);
    }

    public static Stream<Vec3> relativePointsToRotations(float xRot, float yRot, List<Vec3> list)
    {
        Vec3 dir = createMotionVector(xRot, yRot, 1f);

        Vec3 vecZ = createMotionVector(0f, yRot, 1f);
        Vec3 vecX = vecZ.cross(Y_UNIT_VEC);

        float yRotRad = toRad(yRot);
        float sinY = Mth.sin(yRotRad);
        float cosY = Mth.cos(yRotRad);

        return list.stream().map(v -> {
            double nx = (dir.x * v.z) + (vecX.x * v.x) + (-vecZ.x * v.y * cosY);
            double ny = (dir.y * v.z) + (vecX.y * v.x) + (-v.y * sinY);
            double nz = (dir.z * v.z) + (vecX.z * v.x) + (-vecZ.z * v.y * cosY);
            return new Vec3(nx, ny, nz);
        });
    }

    public static Vec3 createMotionVector(LivingEntity entity, double length)
    {
        return createMotionVector(entity.getViewXRot(1f), entity.getViewYRot(1f), length);
    }

    public static Vec3 createMotionVector(LivingEntity entity, double length, double inaccuracy)
    {
        return createMotionVector(entity.getViewXRot(1f), entity.getViewYRot(1f), length, inaccuracy);
    }

    public static Vec3 createMotionVector(Vector2f angles, double length)
    {
        return createMotionVector(angles.x, angles.y, length);
    }

    public static Vec3 createMotionVector(Vector2f angles, double length, double inaccuracy)
    {
        return createMotionVector(angles.x, angles.y, length, inaccuracy);
    }

    public static Vec3 createMotionVector(float xRot, float yRot, double length)
    {
        float xRotRad = -toRad(xRot);
        float yRotRad = -toRad(yRot) - Mth.PI;

        float cosY = Mth.cos(yRotRad);
        float sinY = Mth.sin(yRotRad);
        float cosX = -Mth.cos(xRotRad);
        float sinX = Mth.sin(xRotRad);

        return new Vec3(sinY * cosX * length, sinX * length, cosY * cosX * length);
    }

    public static Vec3 createMotionVector(float xRot, float yRot, double length, double inaccuracy)
    {
        float xRotRad = -toRad(xRot);
        float yRotRad = -toRad(yRot) - Mth.PI;
        inaccuracy *= 0.0172275d;

        float cosY = Mth.cos(yRotRad);
        float sinY = Mth.sin(yRotRad);
        float cosX = -Mth.cos(xRotRad);
        float sinX = Mth.sin(xRotRad);

        return new Vec3((sinY * cosX + triangle(0, inaccuracy)) * length, (sinX + triangle(0, inaccuracy)) * length, (cosY * cosX + triangle(0, inaccuracy)) * length);
    }

    public static float getXRot(Vec3 direction)
    {
        return toDeg(-Mth.atan2(direction.y(), vec2Length(direction)));
    }

    public static float getYRot(Vec3 direction)
    {
        return toDeg(Mth.atan2(direction.z(), direction.x())) - 90f;
    }

    public static Vector2f xyRotBetweenPoints(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;

        float xRot = toDeg(-Mth.atan2(dy, vec2Length(dx, dz)));
        float yRot = toDeg(Mth.atan2(dz, dx)) - 90f;

        return new Vector2f(xRot, yRot);
    }

    public static Vector2f xyRotBetweenPoints(Vec3 a, Vec3 b)
    {
        return xyRotBetweenPoints(a.x, a.y, a.z, b.x, b.y, b.z);
    }

    public static Vector3f xUnitVec()
    {
        return new Vector3f(1, 0, 0);
    }

    public static Vector3f yUnitVec()
    {
        return new Vector3f(0, 1, 0);
    }

    public static Vector3f zUnitVec()
    {
        return new Vector3f(0, 0, 1);
    }

    public static Vector3f unitVecForAxis(Direction.Axis axis)
    {
        return switch (axis)
        {
            case X -> xUnitVec();
            case Y -> yUnitVec();
            case Z -> zUnitVec();
        };
    }
    //#endregion
}