package liedge.limacore.util;

import com.mojang.math.OctahedralGroup;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public final class LimaShapesUtil
{
    private LimaShapesUtil() { }

    public static VoxelShape sizedBox(double x, double y, double z, double xSize, double ySize, double zSize)
    {
        return Block.box(x, y, z, x + xSize, y + ySize, z + zSize);
    }

    public static ShapeBuilder shapeBuilder()
    {
        return new ShapeBuilder();
    }

    public static VoxelShape move(VoxelShape shape, double dx, double dy, double dz)
    {
        ShapeBuilder builder = new ShapeBuilder();

        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) ->
        {
            VoxelShape box = Shapes.box(x1 + dx, y1 + dy, z1 + dz, x2 + dx, y2 + dy, z2 + dz);
            builder.shapes.add(box);
        });

        return builder.build();
    }

    public static List<AABB> toLevelBoundingBoxes(VoxelShape shape, BlockPos pos)
    {
        List<AABB> list = new ObjectArrayList<>();

        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) ->
        {
            AABB bb = new AABB(x1 + pos.getX(), y1 + pos.getY(), z1 + pos.getZ(), x2 + pos.getX(), y2 + pos.getY(), z2 + pos.getZ());
            list.add(bb);
        });

        return list;
    }

    public static OctahedralGroup getYRotation(Direction direction)
    {
        return switch (direction)
        {
            case NORTH -> OctahedralGroup.IDENTITY;
            case EAST -> OctahedralGroup.BLOCK_ROT_Y_90;
            case SOUTH -> OctahedralGroup.BLOCK_ROT_Y_180;
            case WEST -> OctahedralGroup.BLOCK_ROT_Y_270;
            default -> throw new IllegalArgumentException(direction + " is not a horizontal direction.");
        };
    }

    public static final class ShapeBuilder
    {
        private final List<VoxelShape> shapes = new ObjectArrayList<>();

        private ShapeBuilder() { }

        public ShapeBuilder add(VoxelShape shape)
        {
            shapes.add(shape);
            return this;
        }

        public ShapeBuilder absBox(double x1, double y1, double z1, double x2, double y2, double z2)
        {
            return add(Block.box(x1, y1, z1, x2, y2, z2));
        }

        public ShapeBuilder box(double x, double y, double z, double xSize, double ySize, double zSize)
        {
            return add(sizedBox(x, y, z, xSize, ySize, zSize));
        }

        public VoxelShape build()
        {
            return shapes.stream().reduce(Shapes::or).orElse(Shapes.empty());
        }
    }
}