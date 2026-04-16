package liedge.limacore.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import liedge.limacore.LimaCore;
import liedge.limacore.util.LimaJsonUtil;
import net.minecraft.client.resources.model.cuboid.CuboidModelElement;
import net.minecraft.client.resources.model.cuboid.UnbakedCuboidGeometry;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.model.AbstractUnbakedModel;
import net.neoforged.neoforge.client.model.ExtendedUnbakedGeometry;
import net.neoforged.neoforge.client.model.StandardModelParameters;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class ExtendedCuboidModel extends AbstractUnbakedModel
{
    public static final Identifier LOADER_ID = LimaCore.RESOURCES.id("extended_cuboid");
    public static final Loader LOADER = new Loader();

    public static final String KEY_FORCE_EMISSIVE = "force_emissive";
    public static final String KEY_PIPELINE = "pipeline";

    private final @Nullable UnbakedGeometry elements;
    private final boolean forceEmissive;
    private final ItemModelPipeline modelPipeline;

    private ExtendedCuboidModel(StandardModelParameters parameters, @Nullable UnbakedGeometry elements, boolean forceEmissive, ItemModelPipeline modelPipeline)
    {
        super(parameters);
        this.elements = elements;
        this.forceEmissive = forceEmissive;
        this.modelPipeline = modelPipeline;
    }

    @Override
    public ExtendedUnbakedGeometry geometry()
    {
        return (textureSlots, baker, state, debugName, additionalProperties) ->
        {
            Identifier parent = parent();
            UnbakedGeometry source;

            if (elements == null && parent != null)
                source = baker.getModel(parent).getTopGeometry();
            else
                source = elements;

            if (source == null) return QuadCollection.EMPTY;

            QuadCollection bakedSource = source.bake(textureSlots, baker, state, debugName, additionalProperties);
            QuadCollection.Builder output = new QuadCollection.Builder();

            for (Direction side : Direction.values())
            {
                swapRenderTypes(bakedSource, output, side);
            }
            swapRenderTypes(bakedSource, output, null);

            return output.build();
        };
    }

    private void swapRenderTypes(QuadCollection source, QuadCollection.Builder output, @Nullable Direction side)
    {
        List<BakedQuad> quads = source.getQuads(side);
        for (BakedQuad quad : quads)
        {
            addFace(output, modelPipeline.apply(quad, forceEmissive), side);
        }
    }

    private void addFace(QuadCollection.Builder output, BakedQuad quad, @Nullable Direction side)
    {
        if (side == null)
            output.addUnculledFace(quad);
        else
            output.addCulledFace(side, quad);
    }

    public static final class Loader implements UnbakedModelLoader<ExtendedCuboidModel>
    {
        private Loader() {}

        @Override
        public ExtendedCuboidModel read(JsonObject rootJson, JsonDeserializationContext context) throws JsonParseException
        {
            StandardModelParameters parameters = StandardModelParameters.parse(rootJson, context);
            UnbakedGeometry elements = loadElements(rootJson, context);
            boolean forceEmissive = GsonHelper.getAsBoolean(rootJson, KEY_FORCE_EMISSIVE, false);
            ItemModelPipeline modelPipeline = loadPipeline(rootJson);

            return new ExtendedCuboidModel(parameters, elements, forceEmissive, modelPipeline);
        }

        private ItemModelPipeline loadPipeline(JsonObject rootJson) throws JsonParseException
        {
            if (!rootJson.has(KEY_PIPELINE))
                return ItemModelPipeline.ITEM_PIPELINE;
            else
                return ItemModelPipeline.get(GsonHelper.getAsString(rootJson, KEY_PIPELINE));
        }

        private @Nullable UnbakedGeometry loadElements(JsonObject rootJson, JsonDeserializationContext context) throws JsonParseException
        {
            if (!rootJson.has("elements")) return null;

            List<CuboidModelElement> elements = LimaJsonUtil.mapArray(GsonHelper.getAsJsonArray(rootJson, "elements"), context, CuboidModelElement.class).toList();

            return new UnbakedCuboidGeometry(elements);
        }
    }
}