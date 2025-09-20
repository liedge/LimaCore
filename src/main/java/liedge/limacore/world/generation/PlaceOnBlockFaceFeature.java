package liedge.limacore.world.generation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

import java.util.List;
import java.util.Optional;

@Deprecated(forRemoval = true, since = "1.9.2")
public final class PlaceOnBlockFaceFeature extends Feature<PlaceOnBlockFaceFeature.Configuration>
{
    private static final Codec<TargetBlockState> TARGET_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RuleTest.CODEC.fieldOf("target").forGetter(TargetBlockState::target),
            Direction.CODEC.fieldOf("target_face").forGetter(TargetBlockState::targetFace),
            BlockState.CODEC.fieldOf("state").forGetter(TargetBlockState::state))
            .apply(instance, TargetBlockState::new));
    private static final Codec<Configuration> CONFIGURATION_CODEC = TARGET_CODEC.listOf(1, Integer.MAX_VALUE).xmap(Configuration::new, Configuration::targetStates);

    public PlaceOnBlockFaceFeature()
    {
        super(CONFIGURATION_CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<Configuration> context)
    {
        Configuration config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        if (!level.getBlockState(origin).isAir()) return false;

        Optional<TargetBlockState> result = config.targetStates.stream().filter(target -> {
            BlockState surface = level.getBlockState(origin.offset(target.targetFace.getOpposite().getNormal()));
            return target.target.test(surface, context.random()) && target.state.canSurvive(level, origin);
        }).findFirst();

        if (result.isPresent())
        {
            level.setBlock(origin, result.get().state, 2);
            return true;
        }

        return false;
    }

    public record Configuration(List<TargetBlockState> targetStates) implements FeatureConfiguration { }

    public record TargetBlockState(RuleTest target, Direction targetFace, BlockState state) { }
}