package liedge.limacore.client.model.geometry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.LimaCore;
import liedge.limacore.client.model.baked.BlockLayerBakedModel;
import liedge.limacore.client.model.baked.BlockQuadsGroup;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.limacore.util.LimaJsonUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.ExtraFaceData;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class BlockLayerGeometry extends ElementGroupGeometry
{
    public static final ResourceLocation LOADER_ID = LimaCore.RESOURCES.location("block_layers");
    public static final LimaGeometryLoader<?> LOADER = new Loader();

    private final List<GeometryLayerDefinition> layerDefinitions;

    private BlockLayerGeometry(List<BlockElement> elements, Map<String, IntList> elementGroups, List<GeometryLayerDefinition> layerDefinitions)
    {
        super(elements, elementGroups);
        this.layerDefinitions = layerDefinitions;
    }

    @Override
    protected BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, TextureAtlasSprite particleIcon, RenderTypeGroup modelRenderTypes, List<BlockElement> elements, Map<String, IntList> elementGroups)
    {
        BlockQuadsGroup.Builder masterBuilder = BlockQuadsGroup.builder();
        Map<RenderType, BlockQuadsGroup.Builder> builders = new Object2ObjectOpenHashMap<>();
        List<BakedQuad> normalItemQuads = new ObjectArrayList<>();
        List<BakedQuad> emissiveItemQuads = new ObjectArrayList<>();

        for (GeometryLayerDefinition definition : layerDefinitions)
        {
            RenderTypeGroup rtg = definition.renderTypeName() != null ? getRenderTypes(context, definition.renderTypeName()) : modelRenderTypes;
            RenderType layerRenderType = rtg.isEmpty() ? RenderType.SOLID : rtg.block();

            BlockQuadsGroup.Builder builder = builders.computeIfAbsent(layerRenderType, $ -> BlockQuadsGroup.builder());

            IntList elementList = LimaCollectionsUtil.toIntList(definition.groups().stream().flatMapToInt(name -> elementGroups.get(name).intStream()));
            for (int elementIndex : elementList)
            {
                BlockElement element = elements.get(elementIndex);
                for (Direction side : element.faces.keySet())
                {
                    BlockElementFace face = element.faces.get(side);
                    TextureAtlasSprite sprite = spriteGetter.apply(context.getMaterial(face.texture()));
                    BakedQuad quad = BlockModel.bakeFace(element, face, sprite, side, modelState);

                    masterBuilder.addQuad(face.cullForDirection(), quad);
                    builder.addQuad(face.cullForDirection(), quad);

                    if (definition.hasEmissivity())
                    {
                        QuadTransformers.settingEmissivity(definition.emissivity()).processInPlace(quad);
                        emissiveItemQuads.add(quad);
                    }
                    else
                    {
                        normalItemQuads.add(quad);
                    }
                }
            }
        }

        return new BlockLayerBakedModel(context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(), particleIcon, context.getTransforms(), overrides, normalItemQuads, emissiveItemQuads, masterBuilder, builders);
    }

    private static class Loader extends GeometryLoader
    {
        @Override
        protected ElementGroupGeometry createGeometry(JsonObject rootJson, List<BlockElement> elements, Map<String, IntList> elementGroups) throws JsonParseException
        {
            JsonArray layersArray = GsonHelper.getAsJsonArray(rootJson, "layers");
            List<GeometryLayerDefinition> layerDefinitions = new ObjectArrayList<>();

            for (JsonElement je : layersArray)
            {
                if (je.isJsonObject())
                {
                    JsonObject layerJson = je.getAsJsonObject();
                    List<String> groups = LimaJsonUtil.stringsArrayStream(GsonHelper.getAsJsonArray(layerJson, "groups")).filter(elementGroups::containsKey).toList();
                    int emissivity = GsonHelper.getAsInt(layerJson, "emissivity", 0);
                    ResourceLocation rtn = layerJson.has("render_type") ? LimaJsonUtil.getAsResourceLocation(layerJson, "render_type") : null;

                    boolean disableShading = GsonHelper.getAsBoolean(layerJson, "disable_shading", emissivity > 0);
                    if (disableShading)
                    {
                        IntSet targetedElements = LimaCollectionsUtil.toIntSet(groups.stream().flatMapToInt(name -> elementGroups.get(name).intStream()));
                        for (int elementIndex : targetedElements)
                        {
                            BlockElement element = elements.get(elementIndex);
                            //noinspection DataFlowIssue
                            ExtraFaceData faceData = Objects.requireNonNullElse(element.getFaceData(), ExtraFaceData.DEFAULT);
                            elements.set(elementIndex, new BlockElement(element.from, element.to, element.faces, element.rotation, false, faceData));
                        }
                    }

                    layerDefinitions.add(new GeometryLayerDefinition(null, groups, emissivity, rtn));
                }
                else if (je.isJsonArray())
                {
                    JsonArray layerJson = je.getAsJsonArray();
                    List<String> groups = LimaJsonUtil.stringsArrayStream(layerJson).filter(elementGroups::containsKey).toList();
                    layerDefinitions.add(new GeometryLayerDefinition(null, groups, 0, null));
                }
            }

            return new BlockLayerGeometry(elements, elementGroups, layerDefinitions);
        }

        @Override
        public ResourceLocation getLoaderId()
        {
            return LOADER_ID;
        }
    }
}