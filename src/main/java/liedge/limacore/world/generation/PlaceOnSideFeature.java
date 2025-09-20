package liedge.limacore.world.generation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.LimaCore;
import liedge.limacore.registry.game.LimaCoreWorldGen;
import liedge.limacore.util.LimaCollectionsUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class PlaceOnSideFeature extends Feature<PlaceOnSideFeature.Configuration>
{
    public static ConfiguredFeature<Configuration, PlaceOnSideFeature> placeOnSide(BlockStateProvider toPlace, Direction side)
    {
        return new ConfiguredFeature<>(LimaCoreWorldGen.PLACE_ON_SIDE_FEATURE.get(), new Configuration(toPlace, BlockStateProperties.FACING, List.of(side)));
    }

    public static ConfiguredFeature<Configuration, PlaceOnSideFeature> placeOnSides(BlockStateProvider toPlace, Direction... sides)
    {
        return new ConfiguredFeature<>(LimaCoreWorldGen.PLACE_ON_SIDE_FEATURE.get(), new Configuration(toPlace, BlockStateProperties.FACING, List.of(sides)));
    }

    public static ConfiguredFeature<Configuration, PlaceOnSideFeature> placeOnAnySide(BlockStateProvider toPlace)
    {
        return placeOnSides(toPlace, Direction.values());
    }

    public static ConfiguredFeature<Configuration, PlaceOnSideFeature> placeOnAnyHorizontalSide(BlockStateProvider toPlace)
    {
        return placeOnSides(toPlace, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
    }

    public PlaceOnSideFeature()
    {
        super(Configuration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<Configuration> context)
    {
        Configuration config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        BlockState toPlace = config.toPlace.getState(context.random(), origin);
        DirectionProperty facingProperty = findProperty(toPlace, config.facingProperty);

        if (facingProperty == null) return false;

        // Random direction check
        List<Direction> facesToCheck = LimaCollectionsUtil.shuffledList(config.validFaces, context.random());

        for (Direction side : facesToCheck)
        {
            if (!facingProperty.getPossibleValues().contains(side))
            {
                LimaCore.LOGGER.warn("Feature configuration contains a target face not valid for block state: {}", toPlace);
                continue;
            }

            BlockState orientedToPlace = toPlace.setValue(facingProperty, side);
            BlockPos toPlacePos = origin.relative(side);

            if (level.getBlockState(toPlacePos).canBeReplaced() && orientedToPlace.canSurvive(level, toPlacePos))
            {
                level.setBlock(toPlacePos, orientedToPlace, Block.UPDATE_CLIENTS);
                return true;
            }
        }

        return false;
    }

    @Nullable
    private static DirectionProperty findProperty(BlockState state, String propertyName)
    {
        return state.getProperties().stream()
                .filter(p -> p.getName().equals(propertyName))
                .filter(p -> p instanceof DirectionProperty)
                .map(p -> (DirectionProperty) p).findAny().orElse(null);
    }

    public record Configuration(BlockStateProvider toPlace, String facingProperty, List<Direction> validFaces) implements FeatureConfiguration
    {
        private static final Codec<Configuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        BlockStateProvider.CODEC.fieldOf("to_place").forGetter(Configuration::toPlace),
                        Codec.STRING.fieldOf("facing_property").forGetter(Configuration::facingProperty),
                        Direction.CODEC.listOf(1, 6).fieldOf("valid_faces").forGetter(Configuration::validFaces))
                .apply(instance, Configuration::new));

        public Configuration(BlockStateProvider toPlace, DirectionProperty property, List<Direction> validFaces)
        {
            this(toPlace, property.getName(), validFaces);
        }
    }
}