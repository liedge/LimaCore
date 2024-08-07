package liedge.limacore.util;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class LimaNbtUtil
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final IntSet NUMERIC_TYPES = IntSet.of(
            Tag.TAG_BYTE,
            Tag.TAG_DOUBLE,
            Tag.TAG_FLOAT,
            Tag.TAG_INT,
            Tag.TAG_LONG,
            Tag.TAG_SHORT);

    private LimaNbtUtil() {}

    // Codec functions
    public static <T> Tag codecEncode(Codec<T> codec, T object)
    {
        return codec.encodeStart(NbtOps.INSTANCE, object).getOrThrow(msg -> new RuntimeException(String.format("%s codec failed to encode to NBT tag: %s", codec, msg)));
    }

    public static <T> T codecDecode(Codec<T> codec, Tag tag)
    {
        return codec.decode(NbtOps.INSTANCE, tag).getOrThrow(msg -> new RuntimeException(String.format("%s codec failed to decode NBT tag: %s", codec, msg))).getFirst();
    }

    public static <T> T codecDecode(Codec<T> codec, CompoundTag compoundTag, String key)
    {
        return codecDecode(codec, Objects.requireNonNull(compoundTag.get(key), "Compound tag does not contain tag '" + key + "'"));
    }

    //#region Fallback getters
    public static boolean getAsBoolean(CompoundTag tag, String key, boolean fallback)
    {
        return tag.contains(key, Tag.TAG_BYTE) ? tag.getBoolean(key) : fallback;
    }

    public static int getAsInt(CompoundTag tag, String key, int fallback)
    {
        return tag.contains(key, Tag.TAG_INT) ? tag.getInt(key) : fallback;
    }

    public static float getAsFloat(CompoundTag tag, String key, float fallback)
    {
        return tag.contains(key, Tag.TAG_FLOAT) ? tag.getFloat(key) : fallback;
    }

    public static double getAsDouble(CompoundTag tag, String key, double fallback)
    {
        return tag.contains(key, Tag.TAG_DOUBLE) ? tag.getDouble(key) : fallback;
    }

    public static ResourceLocation getAsResourceLocation(CompoundTag tag, String key)
    {
        if (tag.contains(key, Tag.TAG_STRING))
        {
            return ResourceLocation.parse(tag.getString(key));
        }
        else
        {
            throw new IllegalArgumentException("'" + key + "' is not a String value in compound tag");
        }
    }

    public static <E extends Enum<E>> E getAsEnum(CompoundTag tag, String key, Class<E> enumClass)
    {
        int ordinal = Mth.clamp(getAsInt(tag, key, 0), 0, enumClass.getEnumConstants().length);
        return enumClass.getEnumConstants()[ordinal];
    }

    public static <E extends Enum<E>> E getAsEnum(CompoundTag tag, String key, Class<E> enumClass, E fallback)
    {
        int ordinal = getAsInt(tag, key, -1);
        if (ordinal >= 0 && ordinal < enumClass.getEnumConstants().length)
        {
            return enumClass.getEnumConstants()[ordinal];
        }
        else
        {
            return fallback;
        }
    }
    //#endregion

    //#region INBTSerializable helpers
    @SuppressWarnings("unchecked")
    private static <T extends Tag> void deserializeUnchecked(INBTSerializable<T> serializable, HolderLookup.Provider registries, byte tagType, @Nullable Tag rawTag)
    {
        if (rawTag == null || rawTag.getId() != tagType)
        {
            throw new IllegalArgumentException("Null or mismatching tag type");
        }

        serializable.deserializeNBT(registries, (T) rawTag);
    }

    public static void deserializeInt(INBTSerializable<IntTag> serializable, HolderLookup.Provider registries, @Nullable Tag rawTag)
    {
        deserializeUnchecked(serializable, registries, Tag.TAG_INT, rawTag);
    }

    public static void deserializeString(INBTSerializable<StringTag> serializable, HolderLookup.Provider registries, @Nullable Tag rawTag)
    {
        deserializeUnchecked(serializable, registries, Tag.TAG_STRING, rawTag);
    }

    public static void deserializeList(INBTSerializable<ListTag> serializable, HolderLookup.Provider registries, @Nullable Tag rawTag)
    {
        deserializeUnchecked(serializable, registries, Tag.TAG_LIST, rawTag);
    }
    //#endregion

    //#region Optional (Nullable) accessors
    public static @Nullable UUID getOptionalUUID(CompoundTag tag, String key)
    {
        return tag.contains(key, Tag.TAG_INT_ARRAY) ? tag.getUUID(key) : null;
    }

    public static void putOptionalUUID(CompoundTag tag, String key, @Nullable UUID uuid)
    {
        if (uuid != null) tag.putUUID(key, uuid);
    }

    public static @Nullable ResourceLocation getOptionalResourceLocation(CompoundTag tag, String key)
    {
        return tag.contains(key, Tag.TAG_STRING) ? ResourceLocation.parse(tag.getString(key)) : null;
    }

    public static void putOptionalResourceLocation(CompoundTag tag, String key, @Nullable ResourceLocation resourceLocation)
    {
        if (resourceLocation != null) tag.putString(key, resourceLocation.toString());
    }
    //#endregion

    // For use in data generation
    @SuppressWarnings("UnstableApiUsage,deprecation")
    public static CompletableFuture<Void> saveCompressedNbt(CachedOutput cache, CompoundTag tag, Path path)
    {
        return CompletableFuture.runAsync(() -> {
            try
            {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                HashingOutputStream hashStream = new HashingOutputStream(Hashing.sha1(), byteStream);
                NbtIo.writeCompressed(tag, hashStream);
                hashStream.close();
                cache.writeIfNeeded(path, byteStream.toByteArray(), hashStream.hash());
            }
            catch (IOException ex)
            {
                DataProvider.LOGGER.error("Failed to save file to {}", path, ex);
            }
        }, Util.backgroundExecutor());
    }
}