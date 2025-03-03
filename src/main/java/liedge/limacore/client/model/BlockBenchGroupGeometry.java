package liedge.limacore.client.model;

import com.google.gson.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.util.LimaJsonUtil;
import liedge.limacore.util.LimaStreamsUtil;
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
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;

public abstract class BlockBenchGroupGeometry implements IUnbakedGeometry<BlockBenchGroupGeometry>
{
    public static final String DEFAULT_GROUP_NAME = "group";

    protected final List<BlockElement> elements;
    protected final List<BlockBenchGroupData> groups;

    protected BlockBenchGroupGeometry(List<BlockElement> elements, List<BlockBenchGroupData> groups)
    {
        this.elements = elements;
        this.groups = groups;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides)
    {
        TextureAtlasSprite particleIcon = spriteGetter.apply(context.getMaterial("particle"));
        ResourceLocation hint = context.getRenderTypeHint();
        RenderTypeGroup renderTypeGroup = hint != null ? context.getRenderType(hint) : RenderTypeGroup.EMPTY;
        Map<String, BakedQuadGroup.Builder> builders = new Object2ObjectOpenHashMap<>();

        for (BlockBenchGroupData group : groups)
        {
            final boolean hasEmissivity = group.hasEmissivity();
            final int emissivity = group.emissivity();
            BakedQuadGroup.Builder builder = builders.computeIfAbsent(group.name(), name -> BakedQuadGroup.builder());

            for (int elementIndex : group.elements())
            {
                BlockElement element = elements.get(elementIndex);

                for (Direction side : element.faces.keySet())
                {
                    BlockElementFace face = element.faces.get(side);
                    TextureAtlasSprite sprite = spriteGetter.apply(context.getMaterial(face.texture()));
                    BakedQuad quad = BlockModel.bakeFace(element, face, sprite, side, modelState);

                    if (hasEmissivity) QuadTransformers.settingEmissivity(emissivity).processInPlace(quad);

                    if (face.cullForDirection() != null)
                    {
                        builder.addCulledFace(face.cullForDirection(), quad, hasEmissivity);
                    }
                    else
                    {
                        builder.addUnculledFace(quad, hasEmissivity);
                    }
                }
            }
        }

        Object2ObjectMap<String, BakedQuadGroup> quadGroups = builders.entrySet().stream().collect(LimaStreamsUtil.toUnmodifiableObject2ObjectMap(Map.Entry::getKey, e -> e.getValue().build()));

        return bakeGroups(quadGroups, context, particleIcon, renderTypeGroup, overrides);
    }

    protected abstract BakedModel bakeGroups(Map<String, BakedQuadGroup> quadGroups, IGeometryBakingContext context, TextureAtlasSprite particleIcon, RenderTypeGroup renderTypeGroup, ItemOverrides overrides);

    protected static abstract class GeometryLoader implements LimaGeometryLoader<BlockBenchGroupGeometry>
    {
        @Override
        public BlockBenchGroupGeometry read(JsonObject json, JsonDeserializationContext ctx) throws JsonParseException
        {
            JsonArray elementsArray = GsonHelper.getAsJsonArray(json, "elements");
            List<BlockBenchGroupData> groups = new ObjectArrayList<>();

            JsonArray groupsArray = GsonHelper.getAsJsonArray(json, "groups");

            // Read top-level group
            Queue<JsonObject> groupQueue = new ArrayDeque<>();
            IntList topLevelElements = new IntArrayList();
            for (JsonElement element : groupsArray)
            {
                // Add group objects
                if (element.isJsonObject())
                {
                    groupQueue.add(element.getAsJsonObject());
                }
                else if (element.isJsonPrimitive()) // Add top level elements as a 'base' group
                {
                    topLevelElements.add(element.getAsInt());
                }
                else
                {
                    throw new JsonParseException("Invalid json element type in groups object: only int or JsonObject types allowed.");
                }
            }

            // Add top-level group (elements are non-emissive by default)
            groups.add(new BlockBenchGroupData(DEFAULT_GROUP_NAME, topLevelElements));

            // Parse groups (subgroups are added to the queue until no groups are left)
            for (JsonObject current = groupQueue.poll(); current != null; current = groupQueue.poll())
            {
                IntList groupElements = new IntArrayList();

                for (JsonElement je : GsonHelper.getAsJsonArray(current, "children"))
                {
                    if (je.isJsonObject())
                    {
                        groupQueue.add(je.getAsJsonObject());
                    }
                    else if (je.isJsonPrimitive())
                    {
                        groupElements.add(je.getAsInt());
                    }
                }

                if (!groupElements.isEmpty())
                {
                    String groupName = GsonHelper.getAsString(current, "name", DEFAULT_GROUP_NAME);
                    int emissivity = GsonHelper.getAsInt(current, "emissivity", 0);
                    groups.add(new BlockBenchGroupData(groupName, groupElements, emissivity));
                }
            }

            List<BlockElement> elements = deserializeElements(groups, elementsArray, json, ctx);

            return createGeometry(json, elements, groups);
        }

        protected List<BlockElement> deserializeElements(List<BlockBenchGroupData> groups, JsonArray elementsArray, JsonObject modelJson, JsonDeserializationContext ctx)
        {
            // Automatically applies shading to emissive elements
            if (GsonHelper.getAsBoolean(modelJson, "use_auto_emissive_shade", true))
            {
                // Emissive elements are unshaded (not affected by light shader AKA 'true' full-bright) by default
                boolean emissiveShade = GsonHelper.getAsBoolean(modelJson, "emissive_shade", false);
                groups.stream()
                        .filter(BlockBenchGroupData::hasEmissivity)
                        .flatMapToInt(o -> o.elements().intStream())
                        .mapToObj(i -> elementsArray.get(i).getAsJsonObject())
                        .filter(jo -> !jo.has("shade"))
                        .forEach(jo -> jo.addProperty("shade", emissiveShade));
            }

            return LimaJsonUtil.mapArray(elementsArray, ctx, BlockElement.class).toList();
        }

        protected abstract BlockBenchGroupGeometry createGeometry(JsonObject modelJson, List<BlockElement> elements, List<BlockBenchGroupData> groups);
    }
}