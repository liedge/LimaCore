package liedge.limacore.util;

import liedge.limacore.lib.math.LimaCoreMath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

import static liedge.limacore.util.LimaCoreObjects.tryCast;

public final class LimaBlockUtil
{
    private static final double DEFAULT_CHUNK_TRACE_DISTANCE = 8d;

    private LimaBlockUtil() {}

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
     * {@link LimaCoreObjects#tryCast(Class, Object)}
     */
    public static <BE> @Nullable BE getSafeBlockEntity(@Nullable LevelReader level, BlockPos blockPos, Class<BE> beClass)
    {
        return tryCast(beClass, getSafeBlockEntity(level, blockPos));
    }

    public static <BE> @Nullable BE getBlockEntity(@Nullable BlockGetter level, BlockPos pos, Class<BE> beClass)
    {
        return level != null ? tryCast(beClass, level.getBlockEntity(pos)) : null;
    }

    @SuppressWarnings("deprecation")
    public static @Nullable LevelChunk getSafeLevelChunk(@Nullable LevelReader level, int chunkX, int chunkZ)
    {
        if (level != null && level.hasChunk(chunkX, chunkZ))
        {
            return tryCast(LevelChunk.class, level.getChunk(chunkX, chunkZ));
        }
        else
        {
            return null;
        }
    }

    public static @Nullable LevelChunk getSafeLevelChunk(@Nullable LevelReader level, ChunkPos chunkPos)
    {
        return getSafeLevelChunk(level, chunkPos.x(), chunkPos.z());
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
}