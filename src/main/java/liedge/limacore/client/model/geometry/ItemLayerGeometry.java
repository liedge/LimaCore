package liedge.limacore.client.model.geometry;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import liedge.limacore.LimaCore;
import liedge.limacore.client.model.baked.BakedItemLayer;
import liedge.limacore.client.model.baked.ItemLayerBakedModel;
import liedge.limacore.util.LimaCollectionsUtil;
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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class ItemLayerGeometry extends ElementGroupGeometry
{
    public static final ResourceLocation LOADER_ID = LimaCore.RESOURCES.location("item_layers");
    public static final LimaGeometryLoader<?> LOADER = new Loader();

    private final List<GeometryLayerDefinition> layerDefinitions;
    private final boolean useCustomRenderer;

    private ItemLayerGeometry(List<BlockElement> elements, Map<String, IntList> elementGroups, List<GeometryLayerDefinition> layerDefinitions, boolean useCustomRenderer)
    {
        super(elements, elementGroups);
        this.layerDefinitions = layerDefinitions;
        this.useCustomRenderer = useCustomRenderer;
    }

    @Override
    protected BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, TextureAtlasSprite particleIcon, RenderTypeGroup modelRenderTypes, List<BlockElement> elements, Map<String, IntList> elementGroups)
    {
        Set<String> usedNames = new ObjectOpenHashSet<>();
        List<BakedItemLayer.Builder> layerBuilders = new ObjectArrayList<>();

        for (GeometryLayerDefinition definition : layerDefinitions)
        {
            if (Strings.isNullOrEmpty(definition.name()))
                throw new IllegalStateException("Layer name cannot be missing or empty.");

            if (!usedNames.add(definition.name())) throw new IllegalStateException("Duplicate layer name: " + definition.name());

            BakedItemLayer.Builder layerBuilder = BakedItemLayer.builder(definition.name());
            layerBuilders.add(layerBuilder);

            IntList elementList = LimaCollectionsUtil.toIntList(definition.groups().stream().flatMapToInt(o -> elementGroups.get(o).intStream()));
            for (int elementIndex : elementList)
            {
                BlockElement element = elements.get(elementIndex);
                for (Direction side : element.faces.keySet())
                {
                    BlockElementFace face = element.faces.get(side);
                    TextureAtlasSprite sprite = spriteGetter.apply(context.getMaterial(face.texture()));
                    BakedQuad quad = BlockModel.bakeFace(element, face, sprite, side, modelState);

                    if (definition.hasEmissivity()) QuadTransformers.settingEmissivity(definition.emissivity()).processInPlace(quad);

                    layerBuilder.addQuad(quad);
                }
            }

            RenderTypeGroup layerRenderTypes = definition.renderTypeName() != null ? getRenderTypes(context, definition.renderTypeName()) : modelRenderTypes;
            layerBuilder.setRenderTypes(layerRenderTypes);
        }

        return new ItemLayerBakedModel(context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(), particleIcon, context.getTransforms(), ItemOverrides.EMPTY, useCustomRenderer, layerBuilders);
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
                JsonObject layerJson = je.getAsJsonObject();

                String name = GsonHelper.getAsString(layerJson, "name");
                List<String> groups = LimaJsonUtil.stringsArrayStream(GsonHelper.getAsJsonArray(layerJson, "groups")).filter(elementGroups::containsKey).toList();
                int emissivity = GsonHelper.getAsInt(layerJson, "emissivity", 0);
                ResourceLocation rtn = layerJson.has("render_type") ? LimaJsonUtil.getAsResourceLocation(layerJson, "render_type") : null;
                layerDefinitions.add(new GeometryLayerDefinition(name, groups, emissivity, rtn));
            }

            boolean useCustomRenderer = GsonHelper.getAsBoolean(rootJson, "custom_renderer", false);

            return new ItemLayerGeometry(elements, elementGroups, layerDefinitions, useCustomRenderer);
        }

        @Override
        public ResourceLocation getLoaderId()
        {
            return LOADER_ID;
        }
    }
}