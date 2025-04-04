package liedge.limacore.world.generation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import liedge.limacore.LimaCore;
import liedge.limacore.registry.game.LimaCoreWorldGen;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.limacore.util.LimaMathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.ONLY_IN_AIR_PREDICATE;

public final class OnSurfaceHeightRangePlacement extends PlacementModifier
{
    private static final List<Direction> ALL_SIDES = List.of(Direction.values());

    public static final MapCodec<OnSurfaceHeightRangePlacement> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            VerticalAnchor.CODEC.fieldOf("bottom").forGetter(o -> o.bottom),
            VerticalAnchor.CODEC.fieldOf("top").forGetter(o -> o.top),
            Direction.CODEC.listOf(1, Integer.MAX_VALUE).optionalFieldOf("faces_to_check", ALL_SIDES).forGetter(o -> o.facesToCheck),
            Codec.BOOL.optionalFieldOf("alloy_sky_exposure", true).forGetter(o -> o.allowSkyExposure))
            .apply(instance, OnSurfaceHeightRangePlacement::new));

    public static OnSurfaceHeightRangePlacement placeOnSingleFaceBetween(VerticalAnchor bottom, VerticalAnchor top, Direction face, boolean allowSkyExposure)
    {
        return new OnSurfaceHeightRangePlacement(bottom, top, List.of(face), allowSkyExposure);
    }

    private final VerticalAnchor bottom;
    private final VerticalAnchor top;
    private final List<Direction> facesToCheck;
    private final boolean allowSkyExposure;

    public OnSurfaceHeightRangePlacement(VerticalAnchor bottom, VerticalAnchor top, List<Direction> facesToCheck, boolean allowSkyExposure)
    {
        this.bottom = bottom;
        this.top = top;
        this.facesToCheck = facesToCheck;
        this.allowSkyExposure = allowSkyExposure;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext ctx, RandomSource random, BlockPos origin)
    {
        int y0 = bottom.resolveY(ctx);
        int y1 = top.resolveY(ctx);
        if (y0 > y1)
        {
            LimaCore.LOGGER.warn("Invalid height range [{},{}] for surface placement feature.", y0, y1);
            return Stream.of();
        }

        WorldGenLevel level = ctx.getLevel();
        IntList yLevelsToCheck = LimaCollectionsUtil.toIntList(IntStream.rangeClosed(y0, y1));
        IntLists.shuffle(yLevelsToCheck, LimaMathUtil.RANDOM);
        BlockPos.MutableBlockPos currentPos = origin.mutable();

        for (int yLevel : yLevelsToCheck)
        {
            currentPos.setY(yLevel);
            BlockState state = level.getBlockState(currentPos);

            Optional<BlockPos> optional = facesToCheck.stream()
                    .filter(side -> state.isFaceSturdy(level, currentPos, side))
                    .map(side -> currentPos.offset(side.getNormal()))
                    .filter(bp -> !level.isOutsideBuildHeight(bp) && ONLY_IN_AIR_PREDICATE.test(level, bp) && testSkyExposure(level, bp))
                    .findFirst();

            if (optional.isPresent()) return optional.stream();
        }

        return Stream.of();
    }

    @Override
    public PlacementModifierType<?> type()
    {
        return LimaCoreWorldGen.ON_SURFACE_HEIGHT_RANGE.get();
    }

    private boolean testSkyExposure(WorldGenLevel level, BlockPos pos)
    {
        return allowSkyExposure || !level.canSeeSky(pos);
    }
}