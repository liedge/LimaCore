package liedge.limacore.lib.math;

import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;

import java.util.Comparator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.IntBinaryOperator;

public enum MathOperation implements StringRepresentable, DoubleBinaryOperator, IntBinaryOperator
{
    IDENTITY("identity", 0),
    REPLACE("replace", 1),
    ADD("add", 2),
    MAX("max", 3),
    MIN("min", 3),
    ADD_PERCENT("add_percent", 4),
    MULTIPLY("multiply", 5),
    DIVIDE("divide", 5);

    public static final LimaEnumCodec<MathOperation> CODEC = LimaEnumCodec.create(MathOperation.class);
    public static final Comparator<MathOperation> PRIORITY_COMPARATOR = Comparator.comparingInt(MathOperation::getPriority);

    private final String name;
    private final int priority;

    MathOperation(String name, int priority)
    {
        this.name = name;
        this.priority = priority;
    }

    public int getPriority()
    {
        return priority;
    }

    @Override
    public double applyAsDouble(double left, double right)
    {
        return switch (this)
        {
            case ADD -> left + right;
            case MULTIPLY -> left * right;
            case ADD_PERCENT -> left + (left * right);
            case DIVIDE -> right != 0 ? left / right : 0;
            case MAX -> Math.max(left, right);
            case MIN -> Math.min(left, right);
            case IDENTITY -> left;
            case REPLACE -> right;
        };
    }

    @Override
    public int applyAsInt(int left, int right)
    {
        return switch (this)
        {
            case ADD -> left + right;
            case MULTIPLY -> left * right;
            case ADD_PERCENT -> left + (left * right);
            case DIVIDE -> right != 0 ? left / right : 0;
            case MAX -> Math.max(left, right);
            case MIN -> Math.min(left, right);
            case IDENTITY -> left;
            case REPLACE -> right;
        };
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}