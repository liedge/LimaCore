package liedge.limacore.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class LimaEnumCodec<A extends Enum<A> & StringRepresentable> implements Codec<A>
{
    public static <E extends Enum<E> & StringRepresentable> LimaEnumCodec<E> createStrict(Class<E> enumClass)
    {
        return new LimaEnumCodec<>(enumClass, null);
    }

    public static <E extends Enum<E> & StringRepresentable> LimaEnumCodec<E> createDefaulted(Class<E> enumClass, @NotNull E defaultValue)
    {
        return new LimaEnumCodec<>(enumClass, defaultValue);
    }

    public static <E extends Enum<E> & StringRepresentable> AttachmentType.Builder<E> attachmentBuilder(LimaEnumCodec<E> codec)
    {
        return AttachmentType.builder(() -> Objects.requireNonNull(codec.defaultValue, codec + " doesn't support default values.")).serialize(new FastAttachmentSerializer<>(codec));
    }

    private final String name;
    private final @Nullable A defaultValue;
    private final A[] values;
    private final Map<String, A> nameLookup;
    private final Codec<A> baseCodec;

    private LimaEnumCodec(Class<A> enumClass, @Nullable A defaultValue)
    {
        this.name = "LimaEnumCodec[" + enumClass.getSimpleName() + "]";
        this.defaultValue = defaultValue;
        this.values = Objects.requireNonNull(enumClass.getEnumConstants());

        Object2ObjectMap<String, A> map = new Object2ObjectOpenHashMap<>();
        for (A value : values)
        {
            map.put(value.getSerializedName(), value);
        }
        this.nameLookup = Object2ObjectMaps.unmodifiable(map);

        this.baseCodec = ExtraCodecs.orCompressed(
                Codec.stringResolver(StringRepresentable::getSerializedName, this::byNameInternal),
                ExtraCodecs.idResolverCodec(Enum::ordinal, this::byOrdinalInternal, -1));
    }

    private @Nullable A byNameInternal(String name)
    {
        return nameLookup.getOrDefault(name, defaultValue);
    }

    private @Nullable A byOrdinalInternal(int ordinal)
    {
        if (ordinal >= 0 && ordinal < values.length)
        {
            return values[ordinal];
        }
        else
        {
            return defaultValue;
        }
    }

    public A byName(String name)
    {
        return Objects.requireNonNull(byNameInternal(name), this + " doesn't support default values");
    }

    public A byName(String name, A fallback)
    {
        return Objects.requireNonNullElse(byNameInternal(name), fallback);
    }

    public A byOrdinal(int ordinal)
    {
        return Objects.requireNonNull(byOrdinalInternal(ordinal), this + " doesn't support default values");
    }

    public A byOrdinal(int ordinal, A fallback)
    {
        return Objects.requireNonNullElse(byOrdinalInternal(ordinal), fallback);
    }

    @Override
    public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input)
    {
        return baseCodec.decode(ops, input);
    }

    @Override
    public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix)
    {
        return baseCodec.encode(input, ops, prefix);
    }

    @Override
    public String toString()
    {
        return name;
    }

    private record FastAttachmentSerializer<E extends Enum<E> & StringRepresentable>(LimaEnumCodec<E> codec) implements IAttachmentSerializer<StringTag, E>
    {
        @Override
        public E read(IAttachmentHolder holder, StringTag tag, HolderLookup.Provider provider)
        {
            return codec.byName(tag.getAsString());
        }

        @Override
        public StringTag write(E attachment, HolderLookup.Provider provider)
        {
            return StringTag.valueOf(attachment.getSerializedName());
        }
    }
}