package liedge.limacore.client.model;

import com.google.gson.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.util.LimaJsonUtil;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public abstract class BlockBenchGroupGeometry<T> implements IUnbakedGeometry<BlockBenchGroupGeometry<T>>
{
    protected final List<BlockElement> elements;
    protected final List<T> groups;

    protected BlockBenchGroupGeometry(List<BlockElement> elements, List<T> groups)
    {
        this.elements = elements;
        this.groups = groups;
    }

    protected static abstract class GeometryLoader<T> implements LimaGeometryLoader<BlockBenchGroupGeometry<T>>
    {
        @Override
        public BlockBenchGroupGeometry<T> read(JsonObject json, JsonDeserializationContext ctx) throws JsonParseException
        {
            JsonArray elementsArray = GsonHelper.getAsJsonArray(json, "elements");
            List<T> groups = new ObjectArrayList<>();

            List<JsonObject> topLevelGroups = LimaJsonUtil.arrayStream(GsonHelper.getAsJsonArray(json, "groups")).filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject).toList();
            if (topLevelGroups.isEmpty()) throw new JsonParseException("BlockBench Groups geometry must contain at least 1 group.");

            Queue<JsonObject> queue = new ArrayDeque<>(topLevelGroups);
            for (JsonObject current = queue.poll(); current != null; current = queue.poll())
            {
                IntList groupElements = new IntArrayList();

                for (JsonElement je : GsonHelper.getAsJsonArray(current, "children"))
                {
                    if (je.isJsonObject())
                    {
                        queue.add(je.getAsJsonObject());
                    }
                    else if (je.isJsonPrimitive())
                    {
                        groupElements.add(je.getAsInt());
                    }
                }

                if (!groupElements.isEmpty()) groups.add(deserializeGroup(current, groupElements));
            }

            List<BlockElement> elements = deserializeElements(groups, elementsArray, json, ctx);

            return createGeometry(json, elements, groups);
        }

        protected List<BlockElement> deserializeElements(List<T> groups, JsonArray elementsArray, JsonObject modelJson, JsonDeserializationContext ctx)
        {
            return LimaJsonUtil.mapArray(elementsArray, ctx, BlockElement.class).toList();
        }

        protected abstract T deserializeGroup(JsonObject json, IntList groupElements);

        protected abstract BlockBenchGroupGeometry<T> createGeometry(JsonObject modelJson, List<BlockElement> elements, List<T> groups);
    }
}