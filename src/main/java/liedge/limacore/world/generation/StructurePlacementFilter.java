package liedge.limacore.world.generation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.game.LimaCoreWorldGen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Map;

public final class StructurePlacementFilter extends PlacementFilter
{
    public static final MapCodec<StructurePlacementFilter> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.STRUCTURE, Structure.DIRECT_CODEC).fieldOf("structures").forGetter(o -> o.structures),
            Codec.BOOL.optionalFieldOf("strict_placement", true).forGetter(o -> o.strictPlacement))
            .apply(instance, StructurePlacementFilter::new));

    private final HolderSet<Structure> structures;
    private final boolean strictPlacement;

    public static StructurePlacementFilter placeInsideStructure(HolderSet<Structure> structures)
    {
        return new StructurePlacementFilter(structures, true);
    }

    public static StructurePlacementFilter placeInSameChunkAsStructure(HolderSet<Structure> structures)
    {
        return new StructurePlacementFilter(structures, false);
    }

    private StructurePlacementFilter(HolderSet<Structure> structures, boolean strictPlacement)
    {
        this.structures = structures;
        this.strictPlacement = strictPlacement;
    }

    @Override
    protected boolean shouldPlace(PlacementContext ctx, RandomSource random, BlockPos origin)
    {
        WorldGenLevel level = ctx.getLevel();

        if (strictPlacement)
        {
            return level.getLevel().structureManager().getStructureWithPieceAt(origin, structures).isValid();
        }
        else
        {
            ChunkAccess chunk = level.getChunk(origin);
            Map<Structure, ?> map = chunk.getAllReferences();

            return structures.stream().map(Holder::value).anyMatch(map::containsKey);
        }
    }

    @Override
    public PlacementModifierType<?> type()
    {
        return LimaCoreWorldGen.STRUCTURE_PLACEMENT.get();
    }
}