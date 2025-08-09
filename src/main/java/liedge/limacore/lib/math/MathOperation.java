package liedge.limacore.lib.math;

import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;

import java.util.Comparator;
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.function.IntBinaryOperator;

public enum MathOperation implements StringRepresentable, DoubleBinaryOperator, IntBinaryOperator
{
    IDENTITY("identity", 0),
    REPLACE("replace", 1),
    ADD("add", 2),
    ADD_PERCENT("add_percent", 4),
    MULTIPLY("multiply", 10),
    DIVIDE("divide", 10),
    // Exclusively for single operations
    MAX("max", 3),
    MIN("min", 3),
    // Exclusively for compounding operations
    ADD_TOTAL_PERCENT("add_total_percent", 5);

    public static final LimaEnumCodec<MathOperation> SINGLE_OP_CODEC = LimaEnumCodec.create(MathOperation.class, List.of(IDENTITY, REPLACE, ADD, ADD_PERCENT, MULTIPLY, DIVIDE, MAX, MIN));
    public static final LimaEnumCodec<MathOperation> COMPOUND_OP_CODEC = LimaEnumCodec.create(MathOperation.class, List.of(IDENTITY, REPLACE, ADD, ADD_PERCENT, MULTIPLY, DIVIDE, ADD_TOTAL_PERCENT));
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
            default -> throw new UnsupportedOperationException("The operation " + getSerializedName() + " is not supported for single operations.");
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
            default -> throw new UnsupportedOperationException("The operation " + getSerializedName() + " is not supported for single operations.");
        };
    }

    public double applyCompoundingDouble(double total, double base, double operand)
    {
        return switch (this)
        {
            case IDENTITY -> total;
            case REPLACE -> operand;
            case ADD -> total + operand;
            case ADD_PERCENT -> total + (base * operand);
            case MULTIPLY -> total * operand;
            case DIVIDE -> operand != 0 ? total / operand : 0;
            case ADD_TOTAL_PERCENT -> total + (total * operand);
            default -> throw new UnsupportedOperationException("The operation " + getSerializedName() + " is not supported for compound operations.");
        };
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}