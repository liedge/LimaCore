package liedge.limacore.util;

import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.Random;

public final class LimaMathUtil
{
    private LimaMathUtil() {}

    // Commonly used values & random
    public static final Random RANDOM = new Random(13L);

    public static final int THOUSAND = 1000;
    public static final int MILLION = 1_000_000;
    public static final int BILLION = 1_000_000_000;

    // Formats
    public static final DecimalFormat FORMAT_PERCENTAGE = new DecimalFormat("#%");
    public static final DecimalFormat FORMAT_COMMA_INT = new DecimalFormat("#,###");
    public static final DecimalFormat FORMAT_2_ROUND_FLOOR = Util.make(new DecimalFormat("#.##"), o -> o.setRoundingMode(RoundingMode.FLOOR));

    public static int parseHexadecimal(String value)
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
        return divisor != 0 ? Mth.lerp(partialTick, dividendStart, dividendEnd) / divisor : 0f;
    }

    public static int nextIntBetweenInclusive(int min, int max)
    {
        return RANDOM.nextInt(min, max + 1);
    }

    public static boolean rollRandomChance(double chance)
    {
        double d = RANDOM.nextDouble();
        return d <= chance;
    }

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