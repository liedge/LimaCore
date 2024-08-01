package liedge.limacore.data.generation;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public interface TagBuilderHelper<T>
{
    TagBuilder getOrCreateRawBuilder(TagKey<T> tagKey);

    default @Nullable Registry<T> getTagRegistry()
    {
        return null;
    }

    default ResourceKey<T> extractKey(T value)
    {
        return LimaRegistryUtil.getNonNullResourceKey(value, Objects.requireNonNull(getTagRegistry(), "Tag provider does not support resolving keys from values."));
    }

    default LimaTagBuilder<T> buildTag(TagKey<T> tagKey)
    {
        return new LimaTagBuilder<>(this, tagKey);
    }

    default void reverseTag(ResourceKey<T> resourceKey, List<TagKey<T>> tagKeys)
    {
        tagKeys.forEach(key -> getOrCreateRawBuilder(key).addElement(resourceKey.location()));
    }

    default void reverseTag(T value, List<TagKey<T>> tagKeys)
    {
        reverseTag(extractKey(value), tagKeys);
    }

    @SuppressWarnings("UnusedReturnValue")
    final class LimaTagBuilder<T>
    {
        private final TagBuilderHelper<T> parent;
        private final TagBuilder builder;
        private final List<ResourceLocation> elements = new ObjectArrayList<>();

        LimaTagBuilder(TagBuilderHelper<T> parent, TagKey<T> tagKey)
        {
            this.parent = parent;
            this.builder = parent.getOrCreateRawBuilder(tagKey);
        }

        public LimaTagBuilder<T> add(ResourceKey<T> key)
        {
            builder.addElement(key.location());
            elements.add(key.location());
            return this;
        }

        public LimaTagBuilder<T> add(T value)
        {
            ResourceKey<T> key = parent.extractKey(value);
            return add(key);
        }

        public LimaTagBuilder<T> add(Holder<T> holder)
        {
            return add(holder.value());
        }

        @SafeVarargs
        public final LimaTagBuilder<T> add(ResourceKey<T>... keys)
        {
            Stream.of(keys).forEach(this::add);
            return this;
        }

        @SafeVarargs
        public final LimaTagBuilder<T> add(T... values)
        {
            Stream.of(values).forEach(this::add);
            return this;
        }

        @SafeVarargs
        public final LimaTagBuilder<T> add(Holder<T>... holders)
        {
            Stream.of(holders).map(Holder::value).forEach(this::add);
            return this;
        }

        public LimaTagBuilder<T> addValues(Collection<? extends T> valueCollection)
        {
            valueCollection.forEach(this::add);
            return this;
        }

        public LimaTagBuilder<T> addHolders(Collection<Holder<T>> holderCollection)
        {
            holderCollection.forEach(this::add);
            return this;
        }

        public LimaTagBuilder<T> addTag(TagKey<T> tag)
        {
            builder.addTag(tag.location());
            return this;
        }

        @SafeVarargs
        public final LimaTagBuilder<T> addTags(TagKey<T>... tags)
        {
            Stream.of(tags).map(TagKey::location).forEach(builder::addTag);
            return this;
        }

        public LimaTagBuilder<T> copyValuesTo(TagKey<T> other)
        {
            TagBuilder otherBuilder = parent.getOrCreateRawBuilder(other);
            elements.forEach(otherBuilder::addElement);
            return this;
        }

        @SafeVarargs
        public final LimaTagBuilder<T> copyValuesTo(TagKey<T>... others)
        {
            Stream.of(others).forEach(this::copyValuesTo);
            return this;
        }
    }
}