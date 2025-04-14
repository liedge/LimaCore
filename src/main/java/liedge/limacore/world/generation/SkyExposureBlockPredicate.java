package liedge.limacore.world.generation;

import com.mojang.serialization.MapCodec;
import liedge.limacore.registry.game.LimaCoreWorldGen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

public final class SkyExposureBlockPredicate implements BlockPredicate
{
    private static final SkyExposureBlockPredicate INSTANCE = new SkyExposureBlockPredicate();
    public static final MapCodec<SkyExposureBlockPredicate> CODEC = MapCodec.unit(INSTANCE);

    public static BlockPredicate blockCanSeeSky()
    {
        return INSTANCE;
    }

    public static BlockPredicate blockCanNotSeeSky()
    {
        return BlockPredicate.not(INSTANCE);
    }

    @Override
    public BlockPredicateType<?> type()
    {
        return LimaCoreWorldGen.SKY_EXPOSURE_PREDICATE.get();
    }

    @Override
    public boolean test(WorldGenLevel level, BlockPos pos)
    {
        return level.canSeeSky(pos);
    }
}