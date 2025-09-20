package liedge.limacore.world.generation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.game.LimaCoreWorldGen;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.IntStream;
import java.util.stream.Stream;

@Deprecated(forRemoval = true, since = "1.9.2")
public final class RandomVerticalScanPlacement extends PlacementModifier
{
    public static final MapCodec<RandomVerticalScanPlacement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(1, 16).fieldOf("radius").forGetter(o -> o.radius),
            BlockPredicate.CODEC.fieldOf("target_condition").forGetter(o -> o.targetCondition),
            BlockPredicate.CODEC.fieldOf("allowed_search_condition").forGetter(o -> o.allowedSearchCondition),
            Codec.BOOL.optionalFieldOf("filter_on_fail", true).forGetter(o -> o.filterOnFail))
            .apply(instance, RandomVerticalScanPlacement::new));

    public static RandomVerticalScanPlacement searchIf(int radius, BlockPredicate targetCondition)
    {
        return new RandomVerticalScanPlacement(radius, targetCondition, BlockPredicate.not(targetCondition), false);
    }

    private final int radius;
    private final BlockPredicate targetCondition;
    private final BlockPredicate allowedSearchCondition;
    private final boolean filterOnFail;

    public RandomVerticalScanPlacement(int radius, BlockPredicate targetCondition, BlockPredicate allowedSearchCondition, boolean filterOnFail)
    {
        this.radius = radius;
        this.targetCondition = targetCondition;
        this.allowedSearchCondition = allowedSearchCondition;
        this.filterOnFail = filterOnFail;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos origin)
    {
        WorldGenLevel level = context.getLevel();

        if (!allowedSearchCondition.test(level, origin))
        {
            return filterOnFail ? Stream.empty() : Stream.of(origin);
        }
        else
        {
            int[] yLevels = IntStream.rangeClosed(origin.getY() - radius, origin.getY() + radius).filter(i -> !level.isOutsideBuildHeight(i)).toArray();
            shuffleArray(yLevels, random);
            BlockPos.MutableBlockPos current = origin.mutable();

            for (int yLevel : yLevels)
            {
                current.setY(yLevel);
                if (targetCondition.test(level, current)) return Stream.of(current.immutable());
            }
        }

        return Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type()
    {
        return LimaCoreWorldGen.RANDOM_VERTICAL_SCAN_PLACEMENT.get();
    }

    private static void shuffleArray(int[] array, RandomSource random)
    {
        for (int i = array.length - 1; i > 0; i--)
        {
            int j = random.nextInt(i + 1);
            int n = array[i];
            array[i] = array[j];
            array[j] = n;
        }
    }
}