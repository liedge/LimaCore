package liedge.limacore.client.model;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import liedge.limacore.client.LimaCoreClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public final class StaticQuads
{
    private static final int MAX_LAYERS = 8;
    private static final Comparator<RenderType> BLENDS_LAST = Comparator.comparing(rt -> rt.pipeline().getColorTargetState().blendFunction().isPresent());

    public static final StaticQuads EMPTY = new StaticQuads(List.of());

    public static StaticQuads get(StandaloneModelKey<StaticQuads> key)
    {
        StaticQuads model = Minecraft.getInstance().getModelManager().getStandaloneModel(key);
        return model != null ? model : EMPTY;
    }

    public static IdentityStandaloneModel<StaticQuads> create(Identifier model)
    {
        return new Unbaked(model);
    }

    // Class def
    private final List<Layer> layers;

    private StaticQuads(List<Layer> layers)
    {
        this.layers = layers;
    }

    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int color, int lightCoords)
    {
        for (Layer layer : layers)
        {
            layer.submit(poseStack, nodeCollector, color, lightCoords);
        }
    }

    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int lightCoords)
    {
        submit(poseStack, nodeCollector, -1, lightCoords);
    }

    public void submitTinted(PoseStack poseStack, SubmitNodeCollector nodeCollector, int[] tints, int lightCoords)
    {
        for (Layer layer : layers)
        {
            layer.submitTinted(poseStack, nodeCollector, tints, lightCoords);
        }
    }

    public Stream<Vector3fc> extents()
    {
        return layers.stream().flatMap(Layer::extents);
    }

    private record Layer(RenderType renderType, List<BakedQuad> quads)
    {
        Stream<Vector3fc> extents()
        {
            return quads.stream().mapMulti((quad, consumer) ->
            {
                for (int i = 0; i < 4; i++)
                {
                    consumer.accept(quad.position(i));
                }
            });
        }

        void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int color, int lightCoords)
        {
            nodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) ->
            {
                for (BakedQuad quad : quads)
                {
                    submitQuad(pose, buffer, quad, color, lightCoords);
                }
            });
        }

        void submitTinted(PoseStack poseStack, SubmitNodeCollector nodeCollector, int[] tints, int lightCoords)
        {
            nodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) ->
            {
                for (BakedQuad quad : quads)
                {
                    int tintColor = getTint(quad.materialInfo(), tints);
                    submitQuad(pose, buffer, quad, tintColor, lightCoords);
                }
            });
        }

        void submitQuad(PoseStack.Pose pose, VertexConsumer buffer, BakedQuad quad, int color, int lightCoords)
        {
            int lightEmission = quad.materialInfo().lightEmission();

            for (int vertex = 0; vertex < 4; vertex++)
            {
                long packedUV = quad.packedUV(vertex);
                Vector3f normal = pose.transformNormal(quad.direction().getUnitVec3f(), new Vector3f());

                buffer.applyBakedNormals(normal, quad.bakedNormals(), vertex, pose.normal());
                buffer
                        .addVertex(pose, quad.position(vertex))
                        .setColor(color)
                        .setUv(UVPair.unpackU(packedUV), UVPair.unpackV(packedUV))
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(LightCoordsUtil.lightCoordsWithEmission(lightCoords, lightEmission))
                        .setNormal(normal.x, normal.y, normal.z);
            }
        }

        private static int getTint(BakedQuad.MaterialInfo material, int[] tints)
        {
            int index = material.isTinted() ? material.tintIndex() : -1;
            return index >= 0 && index < tints.length ? tints[index] : -1;
        }
    }

    private record Unbaked(Identifier model) implements IdentityStandaloneModel<StaticQuads>
    {
        @Override
        public StaticQuads bake(ModelBaker baker, ModelDebugName name)
        {
            ResolvedModel resolved = baker.getModel(model);
            List<BakedQuad> unsorted = resolved.bakeTopGeometry(resolved.getTopTextureSlots(), baker, BlockModelRotation.IDENTITY).getAll();

            Multimap<RenderType, BakedQuad> map = MultimapBuilder.hashKeys().arrayListValues().build();

            for (BakedQuad quad : unsorted)
            {
                if (map.keySet().size() <= MAX_LAYERS)
                {
                    map.put(quad.materialInfo().itemRenderType(), quad);
                }
                else
                {
                    LimaCoreClient.CLIENT_LOGGER.warn("Standalone model {} exceeded the {} render type layer limit, skipping.", name.debugName(), MAX_LAYERS);
                    return EMPTY;
                }
            }

            List<RenderType> sortedKeys = map.keySet().stream().sorted(BLENDS_LAST).toList();
            ObjectList<Layer> layers = new ObjectArrayList<>();

            for (RenderType key : sortedKeys)
            {
                layers.add(new Layer(key, List.copyOf(map.get(key))));
            }

            return new StaticQuads(ObjectLists.unmodifiable(layers));
        }
    }
}