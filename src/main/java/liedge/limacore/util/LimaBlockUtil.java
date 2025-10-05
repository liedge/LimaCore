package liedge.limacore.util;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.math.LimaCoreMath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static liedge.limacore.util.LimaCoreUtil.castOrNull;

public final class LimaBlockUtil
{
    private static final double DEFAULT_CHUNK_TRACE_DISTANCE = 8d;

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
            float cos = Mth.cos(LimaCoreMath.toRad(xRot));
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

    /**
     * Generates a stream from a bounding box (using floor for the minimums and maximums) pre-filtered to contain only block positions
     * in generated/loaded chunks.
     * @param level Level object
     * @param boundingBox The bounding box, usually from an entity
     * @return Stream containing the block positions in the bounding box
     */
    @SuppressWarnings("deprecation")
    public static Stream<BlockPos> betweenClosedStreamSafe(Level level, AABB boundingBox)
    {
        return BlockPos.betweenClosedStream(boundingBox).filter(level::hasChunkAt);
    }

    /**
     * Generates a stream from a bounding box (using floor for the minimums and ceil for the maximums) pre-filtered to contain only block positions
     * in generated/loaded chunks.
     * @param level Level object
     * @param boundingBox The bounding box, usually from an entity
     * @return Stream containing the block positions in the bounding box
     */
    @SuppressWarnings("deprecation")
    public static Stream<BlockPos> betweenClosedStreamSafeCeil(Level level, AABB boundingBox)
    {
        return BlockPos.betweenClosedStream(Mth.floor(boundingBox.minX), Mth.floor(boundingBox.minY), Mth.floor(boundingBox.minZ), Mth.ceil(boundingBox.maxX), Mth.ceil(boundingBox.maxY), Mth.ceil(boundingBox.maxZ)).filter(level::hasChunkAt);
    }

    /**
     * Calls {@link LevelReader#getBlockEntity(BlockPos)} only if {@code level} and {@code blockPos} are both non-null,
     * and if the chunk is loaded.
     * @param level The level to get the block entity from. Can be null.
     * @param blockPos The position of the block entity. Can be null.
     * @return The result of {@link LevelReader#getBlockEntity(BlockPos)}, or null if either {@code level} or {@code blockPos} are null.
    */
    @SuppressWarnings("deprecation")
    public static @Nullable BlockEntity getSafeBlockEntity(@Nullable LevelReader level, BlockPos blockPos)
    {
        if (level != null && level.hasChunkAt(blockPos))
        {
            return level.getBlockEntity(blockPos);
        }

        return null;
    }

    /**
     * Convenience method that wraps the result of {@link LimaBlockUtil#getSafeBlockEntity(LevelReader, BlockPos)} with a call to
     * {@link LimaCoreUtil#castOrNull(Class, Object)}
     */
    public static <BE> @Nullable BE getSafeBlockEntity(@Nullable LevelReader level, BlockPos blockPos, Class<BE> beClass)
    {
        return castOrNull(beClass, getSafeBlockEntity(level, blockPos));
    }

    public static <BE> @Nullable BE getBlockEntity(@Nullable BlockGetter level, BlockPos pos, Class<BE> beClass)
    {
        return level != null ? castOrNull(beClass, level.getBlockEntity(pos)) : null;
    }

    @SuppressWarnings("deprecation")
    public static @Nullable LevelChunk getSafeLevelChunk(@Nullable LevelReader level, int chunkX, int chunkZ)
    {
        if (level != null && level.hasChunk(chunkX, chunkZ))
        {
            return castOrNull(LevelChunk.class, level.getChunk(chunkX, chunkZ));
        }
        else
        {
            return null;
        }
    }

    public static @Nullable LevelChunk getSafeLevelChunk(@Nullable LevelReader level, ChunkPos chunkPos)
    {
        return getSafeLevelChunk(level, chunkPos.x, chunkPos.z);
    }

    /**
     * Checks whether the chunk containing the given {@link Position} is currently loaded.
     * @param level The level or level accessor object.
     * @param point The coordinate to check, usually a {@link Vec3}.
     * @return {@code true} if the chunk is loaded or {@code false} if not.
     */
    public static boolean hasChunkAt(LevelAccessor level, Position point)
    {
        int chunkX = SectionPos.blockToSectionCoord(point.x());
        int chunkZ = SectionPos.blockToSectionCoord(point.z());

        return level.hasChunk(chunkX, chunkZ);
    }

    /**
     * Traces the ray from a provided origin point and vector path, stopping
     * @param level The level or level accessor object.
     * @param origin The origin of the ray trace.
     * @param path The vector path of the ray.
     * @return A new {@link Vec3} object containing the furthest reachable point along the ray.
     */
    public static Vec3 traceLoadedChunks(LevelAccessor level, Vec3 origin, Vec3 path, double stepDistance)
    {
        Vec3 end = origin.add(path);
        double length = origin.distanceTo(end);
        path = path.normalize().scale(stepDistance);

        Vec3 lastValid = origin;
        Vec3 current = lastValid;

        boolean interrupted = false;
        int maxSteps = (int) (length / stepDistance);
        for (int i = 0; i <= maxSteps; i++)
        {
            if (!hasChunkAt(level, current))
            {
                interrupted = true;
                break;
            }

            lastValid = current;
            current = current.add(path);
        }

        return interrupted ? lastValid : end;
    }

    public static Vec3 traceLoadedChunks(LevelAccessor level, Vec3 origin, Vec3 path)
    {
        return traceLoadedChunks(level, origin, path, DEFAULT_CHUNK_TRACE_DISTANCE);
    }

    //#region Voxel shape functions

    /**
     * Utility helper for {@link Block#box(double, double, double, double, double, double)} to create a box voxel shape
     * with x,y,z coordinates and sizes, rather than 2 sets of x,y,z coordinates.
     */
    public static VoxelShape dimensionBox(double x, double y, double z, double xSize, double ySize, double zSize)
    {
        return Block.box(x, y, z, x + xSize, y + ySize, z + zSize);
    }

    /**
     * Gets an angle (in degrees) from a {@link Direction}. Maps north to 0, east to 90, south to 180 and west to 270.
     * @param side The direction to get an angle from. Must be a horizontal direction or an exception will be thrown.
     */
    public static int rotationYFromDirection(Direction side)
    {
        Preconditions.checkArgument(side.getAxis().isHorizontal(), "Direction must be horizontal for Y axis rotation angle.");
        return (int) (side.toYRot() + 180f) % 360;
    }

    public static List<AABB> blockPosShiftedAABBs(VoxelShape shape, BlockPos pos)
    {
        List<AABB> list = new ObjectArrayList<>();
        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> list.add(new AABB(x1 + pos.getX(), y1 + pos.getY(), z1 + pos.getZ(), x2 + pos.getX(), y2 + pos.getY(), z2 + pos.getZ())));
        return list;
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

    public static Map<Direction, VoxelShape> createHorizontalShapeMap(VoxelShape identity)
    {
        return Direction.Plane.HORIZONTAL.stream().collect(LimaStreamsUtil.toUnmodifiableEnumMap(Direction.class, side -> rotateYClockwise(identity, rotationYFromDirection(side))));
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