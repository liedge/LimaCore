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

public record BlockQuadsGroup(Map<Direction, List<BakedQuad>> culledFaces, List<BakedQuad> unculledFaces)
{
    public static Builder builder()
    {
        return new Builder();
    }

    @ApiStatus.Internal
    public BlockQuadsGroup {}

    public List<BakedQuad> getQuads(@Nullable Direction side)
    {
        return side == null ? unculledFaces : culledFaces.get(side);
    }

    public static final class Builder
    {
        private final Map<Direction, ObjectList<BakedQuad>> culledFaces = new EnumMap<>(Direction.class);
        private final ObjectList<BakedQuad> unculledFaces = new ObjectArrayList<>();

        private Builder() {}

        public BlockQuadsGroup build()
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

            return new BlockQuadsGroup(culled, ObjectLists.unmodifiable(unculledFaces));
        }

        public void addQuad(@Nullable Direction side, BakedQuad quad)
        {
            if (side != null)
                getOrCreateFaceList(side).add(quad);
            else
                unculledFaces.add(quad);
        }

        public void copyBuilderData(BlockQuadsGroup.Builder other)
        {
            unculledFaces.addAll(other.unculledFaces);

            for (Direction side : other.culledFaces.keySet())
            {
                getOrCreateFaceList(side).addAll(other.culledFaces.get(side));
            }
        }

        private List<BakedQuad> getOrCreateFaceList(Direction side)
        {
            return culledFaces.computeIfAbsent(side, $ -> new ObjectArrayList<>());
        }
    }
}