package liedge.limacore.lib.math;

import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;

import java.util.function.DoubleBinaryOperator;
import java.util.function.IntBinaryOperator;

public enum MathOperation implements StringRepresentable, DoubleBinaryOperator, IntBinaryOperator
{
    ADDITION("add"),
    MULTIPLICATION("multiply"),
    DIVISION("divide"),
    MAX("max"),
    MIN("min");

    public static final LimaEnumCodec<MathOperation> CODEC = LimaEnumCodec.create(MathOperation.class);

    private final String name;

    MathOperation(String name)
    {
        this.name = name;
    }

    @Override
    public double applyAsDouble(double left, double right)
    {
        return switch (this)
        {
            case ADDITION -> left + right;
            case MULTIPLICATION -> left * right;
            case DIVISION -> right != 0 ? left / right : 0;
            case MAX -> Math.max(left, right);
            case MIN -> Math.min(left, right);
        };
    }

    @Override
    public int applyAsInt(int left, int right)
    {
        return switch (this)
        {
            case ADDITION -> left + right;
            case MULTIPLICATION -> left * right;
            case DIVISION -> right != 0 ? left / right : 0;
            case MAX -> Math.max(left, right);
            case MIN -> Math.min(left, right);
        };
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}