package liedge.limacore.client.model.geometry;

import com.google.gson.*;
import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import liedge.limacore.LimaCore;
import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import liedge.limacore.util.LimaJsonUtil;
import liedge.limacore.util.LimaStreamsUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;

public abstract class ElementGroupGeometry implements IUnbakedGeometry<ElementGroupGeometry>
{
    public static final String DEFAULT_GROUP_NAME = "group";
    public static final ResourceLocation CUSTOM_EMISSIVE_RENDER_TYPE_NAME = LimaCore.RESOURCES.location("custom_emissive");

    public static RenderTypeGroup customEmissiveRenderTypes()
    {
        return new RenderTypeGroup(RenderType.SOLID, LimaCoreRenderTypes.ITEM_POS_TEX_COLOR_SOLID);
    }

    private final List<BlockElement> elements;
    private final Map<String, IntList> elementGroups;

    protected ElementGroupGeometry(List<BlockElement> elements, Map<String, IntList> elementGroups)
    {
        this.elements = elements;
        this.elementGroups = elementGroups;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides)
    {
        // Global model properties
        TextureAtlasSprite particleIcon = spriteGetter.apply(context.getMaterial("particle"));
        ResourceLocation hint = context.getRenderTypeHint();
        RenderTypeGroup modelRenderTypes = hint != null ? context.getRenderType(hint) : RenderTypeGroup.EMPTY;
        Transformation rootTransform = context.getRootTransform();
        if (!rootTransform.isIdentity()) modelState = UnbakedGeometryHelper.composeRootTransformIntoModelState(modelState, rootTransform);

        return bake(context, baker, spriteGetter, modelState, overrides, particleIcon, modelRenderTypes, elements, elementGroups);
    }

    protected RenderTypeGroup getRenderTypes(IGeometryBakingContext context, ResourceLocation name)
    {
        return name.equals(CUSTOM_EMISSIVE_RENDER_TYPE_NAME) ? customEmissiveRenderTypes() : context.getRenderType(name);
    }

    protected abstract BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, TextureAtlasSprite particleIcon, RenderTypeGroup modelRenderTypes, List<BlockElement> elements, Map<String, IntList> elementGroups);

    protected static abstract class GeometryLoader implements LimaGeometryLoader<ElementGroupGeometry>
    {
        @Override
        public ElementGroupGeometry read(JsonObject rootJson, JsonDeserializationContext context) throws JsonParseException
        {
            // Create the element group map with a default group container
            Object2ObjectMap<String, IntList> elementGroups = new Object2ObjectOpenHashMap<>();
            IntList defaultGroupElements = new IntArrayList();
            elementGroups.put(DEFAULT_GROUP_NAME, defaultGroupElements);

            // Read top level groups and load initial queue
            Queue<JsonObject> queue = new ArrayDeque<>();
            for (JsonElement je : GsonHelper.getAsJsonArray(rootJson, "groups"))
            {
                if (je.isJsonObject())
                    queue.add(je.getAsJsonObject());
                else if (je.isJsonPrimitive())
                    defaultGroupElements.add(je.getAsInt());
            }

            // Parse groups (subgroups are added to the queue until no groups are left)
            for (JsonObject current = queue.poll(); current != null; current = queue.poll())
            {
                String name = GsonHelper.getAsString(current, "name", DEFAULT_GROUP_NAME);
                IntList list = elementGroups.computeIfAbsent(name, $ -> new IntArrayList());

                for (JsonElement je : GsonHelper.getAsJsonArray(current, "children"))
                {
                    if (je.isJsonObject())
                        queue.add(je.getAsJsonObject());
                    else if (je.isJsonPrimitive())
                        list.add(je.getAsInt());
                }
            }

            elementGroups.object2ObjectEntrySet().removeIf(entry -> entry.getValue().isEmpty());
            List<BlockElement> elements = LimaJsonUtil.mapArray(GsonHelper.getAsJsonArray(rootJson, "elements"), context, BlockElement.class).collect(LimaStreamsUtil.toObjectList());

            return createGeometry(rootJson, elements, elementGroups);
        }

        protected abstract ElementGroupGeometry createGeometry(JsonObject rootJson, List<BlockElement> elements, Map<String, IntList> elementGroups) throws JsonParseException;
    }
}