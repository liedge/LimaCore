package liedge.limacore.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import liedge.limacore.LimaCore;
import liedge.limacore.util.LimaJsonUtil;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.cuboid.CuboidModelElement;
import net.minecraft.client.resources.model.cuboid.UnbakedCuboidGeometry;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.context.ContextMap;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.model.AbstractUnbakedModel;
import net.neoforged.neoforge.client.model.ExtendedUnbakedGeometry;
import net.neoforged.neoforge.client.model.StandardModelParameters;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public final class EmissiveUnbakedModel extends AbstractUnbakedModel
{
    public static final Identifier LOADER_ID = LimaCore.RESOURCES.id("emissive_item");
    public static final Loader LOADER = new Loader();

    private final @Nullable UnbakedGeometry source;
    private final int emissionTarget;

    private EmissiveUnbakedModel(StandardModelParameters parameters, @Nullable UnbakedGeometry source, int emissionTarget)
    {
        super(parameters);
        this.source = source;
        this.emissionTarget = emissionTarget;
    }

    @Override
    public ExtendedUnbakedGeometry geometry()
    {
        Identifier parent = parent();
        Either<Identifier, @Nullable UnbakedGeometry> geometrySource;

        if (source == null && parent != null) geometrySource = Either.left(parent);
        else geometrySource = Either.right(source);

        return new Geometry(geometrySource, emissionTarget);
    }

    public static final class Loader implements UnbakedModelLoader<EmissiveUnbakedModel>
    {
        private Loader() {}

        @Override
        public EmissiveUnbakedModel read(JsonObject rootJson, JsonDeserializationContext context) throws JsonParseException
        {
            StandardModelParameters parameters = StandardModelParameters.parse(rootJson, context);
            UnbakedGeometry source = loadElements(rootJson, context);
            int emissionTarget = GsonHelper.getAsInt(rootJson, "emission_target", 0);

            return new EmissiveUnbakedModel(parameters, source, emissionTarget);
        }

        private @Nullable UnbakedGeometry loadElements(JsonObject rootJson, JsonDeserializationContext context) throws JsonParseException
        {
            if (!rootJson.has("elements")) return null;

            List<CuboidModelElement> elements = LimaJsonUtil.mapArray(GsonHelper.getAsJsonArray(rootJson, "elements"), context, CuboidModelElement.class).toList();

            return new UnbakedCuboidGeometry(elements);
        }
    }

    private record Geometry(Either<Identifier, @Nullable UnbakedGeometry> source, int emissionTarget) implements EmissiveGeometry
    {
        @Override
        public @Nullable UnbakedGeometry getGeometry(TextureSlots textureSlots, ModelBaker baker, ModelState state, ModelDebugName debugName, ContextMap additionalProperties)
        {
            return source.map(id -> baker.getModel(id).getTopGeometry(), Function.identity());
        }

        @Override
        public RenderType cutoutEmissive(Identifier atlasLocation)
        {
            return NeoForgeRenderTypes.getItemCutoutUnlit(atlasLocation);
        }

        @Override
        public RenderType translucentEmissive(Identifier atlasLocation)
        {
            return NeoForgeRenderTypes.getItemTranslucentUnlit(atlasLocation);
        }
    }
}