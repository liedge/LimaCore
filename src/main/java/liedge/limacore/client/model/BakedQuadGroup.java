package liedge.limacore.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import liedge.limacore.lib.LimaColor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class BakedQuadGroup
{
    public static Builder builder()
    {
        return new Builder();
    }

    public static BakedQuadGroup allOf(Collection<BakedQuadGroup> groups)
    {
        Builder builder = new Builder();

        for (BakedQuadGroup group : groups)
        {
            group.getCulledFaces().forEach((side, faces) -> {
                if (!faces.isEmpty()) builder.getCulledFacePool(side).addAll(faces);
            });

            builder.unculledFaces.addAll(group.unculledFaces);
            builder.nonEmissiveQuads.addAll(group.nonEmissiveQuads);
            builder.emissiveQuads.addAll(group.emissiveQuads);
        }

        return builder.build();
    }

    // Block quads
    private final Map<Direction, List<BakedQuad>> culledFaces;
    private final List<BakedQuad> unculledFaces;

    // Item render pass quads
    private final List<BakedQuad> nonEmissiveQuads;
    private final List<BakedQuad> emissiveQuads;

    private BakedQuadGroup(Map<Direction, List<BakedQuad>> culledFaces, List<BakedQuad> unculledFaces, List<BakedQuad> nonEmissiveQuads, List<BakedQuad> emissiveQuads)
    {
        this.culledFaces = culledFaces;
        this.unculledFaces = unculledFaces;
        this.nonEmissiveQuads = nonEmissiveQuads;
        this.emissiveQuads = emissiveQuads;
    }

    public Map<Direction, List<BakedQuad>> getCulledFaces()
    {
        return culledFaces;
    }

    public List<BakedQuad> getUnculledFaces()
    {
        return unculledFaces;
    }

    public List<BakedQuad> getNonEmissiveQuads()
    {
        return nonEmissiveQuads;
    }

    public List<BakedQuad> getEmissiveQuads()
    {
        return emissiveQuads;
    }

    // Buffer helpers
    public void putItemQuadsInBuffer(PoseStack poseStack, MultiBufferSource bufferSource, RenderType nonEmissiveRenderType, RenderType emissiveRenderType, LimaColor nonEmissiveTint, LimaColor emissiveTint, int packedLight)
    {
        PoseStack.Pose pose = poseStack.last();

        if (!nonEmissiveQuads.isEmpty())
        {
            VertexConsumer buffer = bufferSource.getBuffer(nonEmissiveRenderType);
            for (BakedQuad quad : nonEmissiveQuads)
            {
                buffer.putBulkData(pose, quad, nonEmissiveTint.red(), nonEmissiveTint.green(), nonEmissiveTint.blue(), 1f, packedLight, OverlayTexture.NO_OVERLAY);
            }
        }

        if (!emissiveQuads.isEmpty())
        {
            VertexConsumer buffer = bufferSource.getBuffer(emissiveRenderType);
            for (BakedQuad quad : emissiveQuads)
            {
                buffer.putBulkData(pose, quad, emissiveTint.red(), emissiveTint.green(), emissiveTint.blue(), 1f, packedLight, OverlayTexture.NO_OVERLAY);
            }
        }
    }

    public void putItemQuadsInBuffer(PoseStack poseStack, MultiBufferSource bufferSource, RenderType nonEmissiveRenderType, RenderType emissiveRenderType, int packedLight)
    {
        putItemQuadsInBuffer(poseStack, bufferSource, nonEmissiveRenderType, emissiveRenderType, LimaColor.WHITE, LimaColor.WHITE, packedLight);
    }

    public static final class Builder
    {
        // Block quads
        private final Map<Direction, ObjectList<BakedQuad>> culledFaces = new EnumMap<>(Direction.class);
        private final ObjectList<BakedQuad> unculledFaces = new ObjectArrayList<>();
        private final ObjectList<BakedQuad> nonEmissiveQuads = new ObjectArrayList<>();
        private final ObjectList<BakedQuad> emissiveQuads = new ObjectArrayList<>();

        public BakedQuadGroup build()
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

            return new BakedQuadGroup(culled, ObjectLists.unmodifiable(unculledFaces), ObjectLists.unmodifiable(nonEmissiveQuads), ObjectLists.unmodifiable(emissiveQuads));
        }

        public void addCulledFace(Direction facing, BakedQuad quad, boolean emissive)
        {
            getCulledFacePool(facing).add(quad);
            addItemQuad(quad, emissive);
        }

        public void addUnculledFace(BakedQuad quad, boolean emissive)
        {
            unculledFaces.add(quad);
            addItemQuad(quad, emissive);
        }

        private List<BakedQuad> getCulledFacePool(Direction facing)
        {
            return culledFaces.computeIfAbsent(facing, $ -> new ObjectArrayList<>());
        }

        private void addItemQuad(BakedQuad quad, boolean emissive)
        {
            if (emissive)
            {
                emissiveQuads.add(quad);
            }
            else
            {
                nonEmissiveQuads.add(quad);
            }
        }
    }
}