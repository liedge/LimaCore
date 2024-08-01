package liedge.limacore.util;

import com.google.common.base.Preconditions;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public final class LimaBlockUtil
{
    private LimaBlockUtil() {}

    private static IllegalArgumentException angleError(int angle)
    {
        return new IllegalArgumentException("Invalid rotation angle for shape: " + angle + ". Must be either 0 or an increment of 90 degrees.");
    }

    public static Direction getDirectionFacingPlayer(BlockPlaceContext context)
    {
        Player player = context.getPlayer();

        if (player != null)
        {
            float xRot = player.getViewXRot(1f);
            float cos = Mth.cos(LimaMathUtil.toRad(xRot));
            if (cos > 0.7071f)
            {
                return context.getHorizontalDirection().getOpposite();
            }
            else
            {
                return xRot > 0 ? Direction.UP : Direction.DOWN;
            }
        }

        return context.getClickedFace();
    }

    //#region Voxel shape functions

    /**
     * Gets an angle (in degrees) from a {@link Direction}. Maps north to 0, east to 90, south to 180 and west to 270.
     * @param side The direction to get an angle from. Must be a horizontal direction or an exception will be thrown.
     */
    public static int rotationYFromDirection(Direction side)
    {
        Preconditions.checkArgument(side.getAxis().isHorizontal(), "Direction must be horizontal for Y axis rotation angle.");
        return (int) (side.toYRot() + 180f) % 360;
    }

    public static VoxelShape modifyAndMergeAllBoxes(VoxelShape original, VoxelShapeFactory factory)
    {
        Stream.Builder<VoxelShape> builder = Stream.builder();
        original.forAllBoxes((x1, y1, z1, x2, y2, z2) -> builder.add(factory.createShape(x1, y1, z1, x2, y2, z2)));
        return builder.build().reduce(Shapes.empty(), Shapes::or);
    }

    public static VoxelShape moveShape(VoxelShape original, double dx, double dy, double dz)
    {
        return modifyAndMergeAllBoxes(original, (x1, y1, z1, x2, y2, z2) -> Shapes.box(x1 + dx, y1 + dy, z1 + dz, x2 + dx, y2 + dy, z2 + dz));
    }

    /**
     * Rotates a shape clockwise around the X axis. Clockwise is relative facing south (+X)
     * @param shape The shape to be rotated
     * @param angle The angle at which the shape is rotated. Valid values are 0, 90, 180, and 270. 0 returns the shape without modification.
     * @return The rotated shape
     */
    @SuppressWarnings("SuspiciousNameCombination")
    public static VoxelShape rotateXClockwise(VoxelShape shape, int angle)
    {
        final int fixedAngle = angle % 360;

        return switch (fixedAngle)
        {
            case 0 -> shape;
            case 90 -> modifyAndMergeAllBoxes(shape, (x1, y1, z1, x2, y2, z2) -> Shapes.box(1 - y2, x1, z1, 1 - y1, x2, z2));
            case 180 -> modifyAndMergeAllBoxes(shape, (x1, y1, z1, x2, y2, z2) -> Shapes.box(1 - x2, 1 - y2, z1, 1 - x1, 1 - y1, z2));
            case 270 -> modifyAndMergeAllBoxes(shape, (x1, y1, z1, x2, y2, z2) -> Shapes.box(y1, 1 - x2, z1, y2, 1 - x1, z2));
            default -> throw angleError(fixedAngle);
        };
    }

    /**
     * Rotates a shape clockwise around the Y axis. Clockwise is relative to facing down (-Y).
     * @param shape The shape to be rotated
     * @param angle The angle at which the shape is rotated. Valid values are 0, 90, 180, and 270. 0 returns the shape without modification.
     * @return The rotated shape
     */
    public static VoxelShape rotateYClockwise(VoxelShape shape, int angle)
    {
        final int fixedAngle = angle % 360;

        return switch (fixedAngle)
        {
            case 0 -> shape;
            case 90 -> modifyAndMergeAllBoxes(shape, (x1, y1, z1, x2, y2, z2) -> Shapes.box(1 - z2, y1, x1, 1 - z1, y2, x2));
            case 180 -> modifyAndMergeAllBoxes(shape, (x1, y1, z1, x2, y2, z2) -> Shapes.box(1 - x2, y1, 1 - z2, 1 - x1, y2, 1 - z1));
            case 270 -> modifyAndMergeAllBoxes(shape, (x1, y1, z1, x2, y2, z2) -> Shapes.box(z1, y1, 1 - x2, z2, y2, 1 - x1));
            default -> throw angleError(fixedAngle);
        };
    }

    /**
     * Rotates a shape clockwise around the Z axis. Clockwise is relative facing east (+Z)
     * @param shape The shape to be rotated
     * @param angle The angle at which the shape is rotated. Valid values are 0, 90, 180, and 270. 0 returns the shape without modification.
     * @return The rotated shape
     */
    public static VoxelShape rotateZClockWise(VoxelShape shape, int angle)
    {
        final int fixedAngle = angle % 360;

        return switch (fixedAngle)
        {
            case 0 -> shape;
            case 90 -> modifyAndMergeAllBoxes(shape, (x1, y1, z1, x2, y2, z2) -> Shapes.box(x1, 1 - z2, y1, x2, 1 - z1, y2));
            case 180 -> modifyAndMergeAllBoxes(shape, (x1, y1, z1, x2, y2, z2) -> Shapes.box(x1, 1 - y2, 1 - z2, x2, 1 - y1, 1 - z1));
            case 270 -> modifyAndMergeAllBoxes(shape, (x1, y1, z1, x2, y2, z2) -> Shapes.box(x1, z1, 1 - y2, x2, z2, 1 - y1));
            default -> throw angleError(fixedAngle);
        };

    }

    @FunctionalInterface
    public interface VoxelShapeFactory
    {
        VoxelShape createShape(double x1, double y1, double z1, double x2, double y2, double z2);
    }

    //#endregion
}