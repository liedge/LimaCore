package liedge.limacore.client.model.baked;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public record BakedQuadCollection(Map<Direction, List<BakedQuad>> culledFaces, List<BakedQuad> unculledFaces)
{
    private static final Direction[] QUAD_SIDES = new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN, null};

    public static BakedQuadCollection.Builder builder()
    {
        return new Builder();
    }

    @ApiStatus.Internal
    public BakedQuadCollection
    {}

    public Map<Direction, List<BakedQuad>> getCulledFaces()
    {
        return culledFaces;
    }

    public List<BakedQuad> getUnculledFaces()
    {
        return unculledFaces;
    }

    public List<BakedQuad> getFaces(@Nullable Direction side)
    {
        return side == null ? unculledFaces : culledFaces.getOrDefault(side, List.of());
    }

    public static final class Builder
    {
        private final Map<Direction, ObjectList<BakedQuad>> culledFaces = new EnumMap<>(Direction.class);
        private final ObjectList<BakedQuad> unculledFaces = new ObjectArrayList<>();

        private Builder() {}

        public BakedQuadCollection build()
        {
            Map<Direction, List<BakedQuad>> culled = new EnumMap<>(Direction.class);

            for (Direction side : Direction.values())
            {
                if (culledFaces.containsKey(side) && !culledFaces.get(side).isEmpty())
                {
                    culled.put(side, ObjectLists.unmodifiable(culledFaces.get(side)));
                }
                else
                {
                    culled.put(side, List.of());
                }
            }

            return new BakedQuadCollection(culled, ObjectLists.unmodifiable(unculledFaces));
        }

        public void addFace(@Nullable Direction facing, BakedQuad quad)
        {
            if (facing == null) addUnculledFace(quad);
            else addCulledFace(facing, quad);
        }

        public void addCulledFace(Direction facing, BakedQuad quad)
        {
            getCulledFacePool(facing).add(quad);
        }

        public void addUnculledFace(BakedQuad quad)
        {
            unculledFaces.add(quad);
        }

        private List<BakedQuad> getCulledFacePool(Direction facing)
        {
            return culledFaces.computeIfAbsent(facing, $ -> new ObjectArrayList<>());
        }
    }
}