package liedge.limacore.client.model.geometry;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.LimaCore;
import liedge.limacore.client.model.baked.LimaLayerBakedModel;
import liedge.limacore.util.LimaJsonUtil;
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
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class LimaLayerGeometry extends ElementGroupGeometry
{
    public static final ResourceLocation LOADER_ID = LimaCore.RESOURCES.location("custom_layers");
    public static final LimaGeometryLoader<?> LOADER = new Loader();

    private final List<LayerDefinition> layerDefinitions;
    private final boolean useCustomRenderer;

    private LimaLayerGeometry(List<BlockElement> elements, Map<String, IntList> elementGroups, List<LayerDefinition> layerDefinitions, boolean useCustomRenderer)
    {
        super(elements, elementGroups);
        this.layerDefinitions = layerDefinitions;
        this.useCustomRenderer = useCustomRenderer;
    }

    @Override
    protected BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, TextureAtlasSprite particleIcon, RenderTypeGroup modelRenderTypes, List<BlockElement> elements, Map<String, IntList> elementGroups)
    {
        Map<String, Pair<List<BakedQuad>, RenderTypeGroup>> bakedLayers = new Object2ObjectOpenHashMap<>();

        for (LayerDefinition definition : layerDefinitions)
        {
            List<BakedQuad> layerQuads = new ObjectArrayList<>();

            IntList list = definition.groups.stream()
                    .flatMapToInt(o -> elementGroups.get(o).intStream())
                    .collect(IntArrayList::new, IntList::add, IntList::addAll);

            for (int elementIndex : list)
            {
                BlockElement element = elements.get(elementIndex);
                for (Direction side : element.faces.keySet())
                {
                    BlockElementFace face = element.faces.get(side);
                    TextureAtlasSprite sprite = spriteGetter.apply(context.getMaterial(face.texture()));
                    BakedQuad quad = BlockModel.bakeFace(element, face, sprite, side, modelState);

                    if (definition.hasEmissivity()) QuadTransformers.settingEmissivity(definition.emissivity).processInPlace(quad);

                    layerQuads.add(quad);
                }
            }

            RenderTypeGroup layerRenderTypes = definition.renderTypeNames.map(o -> getRenderTypes(context, o)).orElse(modelRenderTypes);

            bakedLayers.put(definition.name, new Pair<>(layerQuads, layerRenderTypes));
        }

        return new LimaLayerBakedModel(context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(), particleIcon, context.getTransforms(), ItemOverrides.EMPTY, useCustomRenderer, modelRenderTypes, bakedLayers);
    }

    private record LayerDefinition(String name, List<String> groups, int emissivity, Optional<ResourceLocation> renderTypeNames)
    {
        public LayerDefinition
        {
            Preconditions.checkArgument(emissivity >= 0 && emissivity <= 16, "Emissivity must be in range [0,16)");
        }

        public LayerDefinition(String name, List<String> groups, int emissivity, @Nullable ResourceLocation renderTypeNames)
        {
            this(name, groups, emissivity, Optional.ofNullable(renderTypeNames));
        }

        public boolean hasEmissivity()
        {
            return emissivity > 0;
        }
    }

    private static class Loader extends GeometryLoader
    {
        @Override
        protected ElementGroupGeometry createGeometry(JsonObject rootJson, List<BlockElement> elements, Map<String, IntList> elementGroups) throws JsonParseException
        {
            JsonArray layersArray = GsonHelper.getAsJsonArray(rootJson, "layers");
            List<LayerDefinition> layerDefinitions = new ObjectArrayList<>();

            for (JsonElement je : layersArray)
            {
                JsonObject layerJson = je.getAsJsonObject();

                String name = GsonHelper.getAsString(layerJson, "name");
                List<String> groups = LimaJsonUtil.stringsArrayStream(GsonHelper.getAsJsonArray(layerJson, "groups")).filter(elementGroups::containsKey).toList();
                int emissivity = GsonHelper.getAsInt(layerJson, "emissivity", 0);
                ResourceLocation rtn = layerJson.has("render_type") ? LimaJsonUtil.getAsResourceLocation(layerJson, "render_type") : null;
                layerDefinitions.add(new LayerDefinition(name, groups, emissivity, rtn));
            }

            boolean useCustomRenderer = GsonHelper.getAsBoolean(rootJson, "custom_renderer", false);

            return new LimaLayerGeometry(elements, elementGroups, layerDefinitions, useCustomRenderer);
        }

        @Override
        public ResourceLocation getLoaderId()
        {
            return LOADER_ID;
        }
    }
}