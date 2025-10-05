package liedge.limacore.lib.math;

import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;

import java.util.Comparator;
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;

public enum MathOperation implements StringRepresentable, DoubleBinaryOperator
{
    IDENTITY("identity", 0),
    REPLACE("replace", 1),
    ADD("add", 2),
    MULTIPLY("multiply", 10),
    DIVIDE("divide", 10),
    // Exclusively for single operations
    MAX("max", 3),
    MIN("min", 3),
    MULTIPLY_AND_ADD("multiply_and_add", 4),
    // Exclusively for compounding operations
    ADD_PERCENT_OF_BASE("add_percent_of_base", 4),
    ADD_PERCENT_OF_TOTAL("add_percent_of_total", 5);

    public static final LimaEnumCodec<MathOperation> SINGLE_OP_CODEC = LimaEnumCodec.create(MathOperation.class, List.of(IDENTITY, REPLACE, ADD, MULTIPLY_AND_ADD, MULTIPLY, DIVIDE, MAX, MIN));
    public static final LimaEnumCodec<MathOperation> COMPOUND_OP_CODEC = LimaEnumCodec.create(MathOperation.class, List.of(IDENTITY, REPLACE, ADD, ADD_PERCENT_OF_BASE, ADD_PERCENT_OF_TOTAL, MULTIPLY, DIVIDE));
    public static final Comparator<MathOperation> PRIORITY_COMPARATOR = Comparator.comparingInt(MathOperation::getPriority);

    public static Comparator<MathOperation> comparingPriority()
    {
        return PRIORITY_COMPARATOR;
    }

    public static <T> Comparator<T> comparingPriority(Function<? super T, MathOperation> opAccessor)
    {
        return Comparator.comparing(opAccessor, PRIORITY_COMPARATOR);
    }

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
            case MULTIPLY_AND_ADD -> left + (left * right);
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
            case ADD_PERCENT_OF_BASE -> total + (base * operand);
            case ADD_PERCENT_OF_TOTAL -> total + (total * operand);
            case MULTIPLY -> total * operand;
            case DIVIDE -> operand != 0 ? total / operand : 0;
            default -> throw new UnsupportedOperationException("The operation " + getSerializedName() + " is not supported for compound operations.");
        };
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}