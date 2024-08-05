package liedge.limacore.util;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

import java.util.function.BiConsumer;
import java.util.stream.*;

public final class LimaJsonUtil
{
    private static final Logger LOGGER = LogUtils.getLogger();

    private LimaJsonUtil() {}

    //#region Array stream helpers
    public static Stream<JsonElement> arrayStream(JsonArray array)
    {
        return StreamSupport.stream(array.spliterator(), false);
    }

    public static Stream<JsonArray> arraysArrayStream(JsonArray array)
    {
        return arrayStream(array).map(JsonElement::getAsJsonArray);
    }

    public static Stream<JsonObject> objectsArrayStream(JsonArray array)
    {
        return arrayStream(array).map(JsonElement::getAsJsonObject);
    }

    public static Stream<String> stringsArrayStream(JsonArray array)
    {
        return arrayStream(array).map(JsonElement::getAsString);
    }

    public static IntStream intsArrayStream(JsonArray array)
    {
        return arrayStream(array).mapToInt(JsonElement::getAsInt);
    }

    public static DoubleStream doublesArrayStream(JsonArray array)
    {
        return arrayStream(array).mapToDouble(JsonElement::getAsDouble);
    }

    public static <T> Stream<T> mapArray(JsonArray array, JsonDeserializationContext ctx, Class<T> typeOfT)
    {
        return arrayStream(array).map(e -> ctx.deserialize(e, typeOfT));
    }
    //#endregion

    //#region Stream collectors
    public static <T extends JsonElement> Collector<T, JsonArray, JsonArray> toJsonArray()
    {
        return collectAsArray(JsonArray::add);
    }

    public static Collector<String, JsonArray, JsonArray> toJsonStringArray()
    {
        return collectAsArray(JsonArray::add);
    }

    public static Collector<Number, JsonArray, JsonArray> toJsonNumberArray()
    {
        return collectAsArray(JsonArray::add);
    }

    private static <T> Collector<T, JsonArray, JsonArray> collectAsArray(BiConsumer<JsonArray, T> accumulator)
    {
        return Collector.of(JsonArray::new, accumulator, (a1, a2) ->
        {
            a1.addAll(a2);
            return a1;
        });
    }
    //#endregion

    //#region Helper serializers/deserializers
    public static <T> JsonElement codecEncode(Codec<T> codec, T object)
    {
        return codec.encodeStart(JsonOps.INSTANCE, object).getOrThrow(msg -> new RuntimeException(String.format("%s failed to encode to json: %s", codec, msg)));
    }

    public static <T> T codecDecode(Codec<T> codec, JsonElement json)
    {
        return codec.decode(JsonOps.INSTANCE, json).getOrThrow(msg -> new RuntimeException(String.format("%s failed to decode json element: %s", codec, msg))).getFirst();
    }

    public static ResourceLocation getAsResourceLocation(JsonObject json, String key)
    {
        return getAsResourceLocation(json.get(key));
    }

    public static ResourceLocation getAsResourceLocation(JsonElement element)
    {
        if (!GsonHelper.isStringValue(element))
            throw new JsonParseException("Invalid json element type. Excepted string, got: " + GsonHelper.getType(element));

        return ResourceLocation.parse(element.getAsString());
    }

    public static <T> ResourceKey<T> getAsResourceKey(ResourceKey<? extends Registry<T>> registryKey, JsonObject json, String key)
    {
        ResourceLocation location = getAsResourceLocation(json, key);
        return ResourceKey.create(registryKey, location);
    }

    public static <T> ResourceKey<T> getAsResourceKey(ResourceKey<? extends Registry<T>> registryKey, JsonElement element)
    {
        ResourceLocation location = getAsResourceLocation(element);
        return ResourceKey.create(registryKey, location);
    }

    public static JsonElement serializeResourceKey(ResourceKey<?> key)
    {
        return new JsonPrimitive(key.location().toString());
    }

    public static <T> JsonElement serializeRegistryValue(T value, Registry<T> registry)
    {
        ResourceLocation key = LimaRegistryUtil.getNonNullRegistryId(value, registry);
        return new JsonPrimitive(key.toString());
    }

    public static <T> T deserializeRegistryValue(JsonElement element, Registry<T> registry)
    {
        ResourceLocation key = getAsResourceLocation(element);
        return LimaRegistryUtil.getNonNullRegistryValue(key, registry);
    }
    //#endregion
}