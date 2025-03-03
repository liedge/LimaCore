package liedge.limacore.blockentity;

import liedge.limacore.LimaCore;
import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.Translatable;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum RelativeHorizontalSide implements StringRepresentable, Translatable
{
    TOP("top"),
    BOTTOM("bottom"),
    FRONT("front"),
    REAR("rear"),
    LEFT("left"),
    RIGHT("right");

    public static final LimaEnumCodec<RelativeHorizontalSide> CODEC = LimaEnumCodec.create(RelativeHorizontalSide.class);
    public static final StreamCodec<FriendlyByteBuf, RelativeHorizontalSide> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(RelativeHorizontalSide.class);

    public static RelativeHorizontalSide of(Direction facing, Direction side)
    {
        if (side.getAxis().isVertical())
        {
            return side == Direction.UP ? TOP : BOTTOM;
        }

        int facing2D = facing.get2DDataValue();
        int side2D = side.get2DDataValue();
        int delta = (side2D - facing2D + 4) % 4;

        return switch (delta)
        {
            case 0 -> FRONT;
            case 1 -> LEFT;
            case 2 -> REAR;
            case 3 -> RIGHT;
            default -> throw new IllegalArgumentException("Invalid combination of facing and side: " + facing + ", " + side);
        };
    }

    private final String name;
    private final String translationKey;

    RelativeHorizontalSide(String name)
    {
        this.name = name;
        this.translationKey = LimaCore.RESOURCES.translationKey("relative_side", "{}", name);
    }

    public Direction resolveAbsoluteSide(Direction front)
    {
        return switch (this)
        {
            case TOP -> Direction.UP;
            case BOTTOM -> Direction.DOWN;
            case FRONT -> front;
            case REAR -> front.getOpposite();
            case LEFT -> front.getClockWise();
            case RIGHT -> front.getCounterClockWise();
        };
    }

    @Override
    public String getSerializedName()
    {
        return this.name;
    }

    @Override
    public String descriptionId()
    {
        return translationKey;
    }
}