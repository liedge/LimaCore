package liedge.limacore.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import liedge.limacore.util.LimaJsonUtil;
import net.minecraft.client.resources.model.cuboid.CuboidModelElement;
import net.minecraft.client.resources.model.cuboid.UnbakedCuboidGeometry;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.model.AbstractUnbakedModel;
import net.neoforged.neoforge.client.model.StandardModelParameters;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;
import org.jspecify.annotations.Nullable;

import java.util.List;

public abstract class RenderRemapModel extends AbstractUnbakedModel
{
    private final @Nullable UnbakedGeometry elements;
    private final int emissionTarget;

    protected RenderRemapModel(StandardModelParameters parameters, @Nullable UnbakedGeometry elements, int emissionTarget)
    {
        super(parameters);
        this.elements = elements;
        this.emissionTarget = emissionTarget;
    }

    @Override
    public @Nullable UnbakedGeometry geometry()
    {
        Identifier parent = parent();
        Either<Identifier, @Nullable UnbakedGeometry> source;

        if (elements == null && parent != null) source = Either.left(parent);
        else source = Either.right(elements);

        return bakeGeometry(source, emissionTarget);
    }

    protected abstract RenderRemapGeometry bakeGeometry(Either<Identifier, @Nullable UnbakedGeometry> source, int emissionTarget);

    public static final class Loader<T extends RenderRemapModel> implements UnbakedModelLoader<T>
    {
        private final int defaultEmissionTarget;
        private final Factory<T> factory;

        public Loader(int defaultEmissionTarget, Factory<T> factory)
        {
            this.defaultEmissionTarget = defaultEmissionTarget;
            this.factory = factory;
        }

        @Override
        public T read(JsonObject rootJson, JsonDeserializationContext context) throws JsonParseException
        {
            StandardModelParameters parameters = StandardModelParameters.parse(rootJson, context);
            UnbakedGeometry elements = loadElements(rootJson, context);
            int emissionTarget = GsonHelper.getAsInt(rootJson, "emission_target", defaultEmissionTarget);

            return factory.create(parameters, elements, emissionTarget);
        }

        private @Nullable UnbakedGeometry loadElements(JsonObject rootJson, JsonDeserializationContext context) throws JsonParseException
        {
            if (!rootJson.has("elements")) return null;

            List<CuboidModelElement> elements = LimaJsonUtil.mapArray(GsonHelper.getAsJsonArray(rootJson, "elements"), context, CuboidModelElement.class).toList();

            return new UnbakedCuboidGeometry(elements);
        }
    }

    public interface Factory<T extends RenderRemapModel>
    {
        T create(StandardModelParameters parameters, @Nullable UnbakedGeometry elements, int emissionTarget);
    }
}