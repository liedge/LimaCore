package liedge.limacore.world.generation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.game.LimaCoreWorldGen;
import liedge.limacore.util.LimaCollectionsUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class RandomAxisScanPlacement extends PlacementModifier
{
    public static final MapCodec<RandomAxisScanPlacement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Direction.Axis.CODEC.fieldOf("search_axis").forGetter(o -> o.searchAxis),
            Codec.intRange(0, 16).fieldOf("radius").forGetter(o -> o.radius),
            BlockPredicate.CODEC.fieldOf("target_condition").forGetter(o -> o.targetCondition))
            .apply(instance, RandomAxisScanPlacement::new));

    public static RandomAxisScanPlacement vertical(int radius, BlockPredicate targetCondition)
    {
        return new RandomAxisScanPlacement(Direction.Axis.Y, radius, targetCondition);
    }

    public static RandomAxisScanPlacement xAxis(int radius, BlockPredicate targetCondition)
    {
        return new RandomAxisScanPlacement(Direction.Axis.X, radius, targetCondition);
    }

    public static RandomAxisScanPlacement zAxis(int radius, BlockPredicate targetCondition)
    {
        return new RandomAxisScanPlacement(Direction.Axis.Z, radius, targetCondition);
    }

    private final Direction.Axis searchAxis;
    private final int radius;
    private final BlockPredicate targetCondition;

    public RandomAxisScanPlacement(Direction.Axis searchAxis, int radius, BlockPredicate targetCondition)
    {
        this.searchAxis = searchAxis;
        this.radius = radius;
        this.targetCondition = targetCondition;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos origin)
    {
        WorldGenLevel level = context.getLevel();

        final int originN = searchAxis.choose(origin.getX(), origin.getY(), origin.getZ());
        IntStream oStream = IntStream.rangeClosed(originN - radius, originN + radius).filter(i -> !searchAxis.isVertical() || !level.isOutsideBuildHeight(i));
        int[] oArray = LimaCollectionsUtil.shuffleIntArray(oStream.toArray(), random);

        BlockPos.MutableBlockPos cursor = origin.mutable();

        for (int o : oArray)
        {
            switch (searchAxis)
            {
                case X -> cursor.setX(o);
                case Y -> cursor.setY(o);
                case Z -> cursor.setZ(o);
            }

            if (targetCondition.test(level, cursor)) return Stream.of(cursor.immutable());
        }

        return Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type()
    {
        return LimaCoreWorldGen.RANDOM_AXIS_SCAN_PLACEMENT.get();
    }
}