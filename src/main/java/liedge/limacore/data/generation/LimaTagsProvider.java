package liedge.limacore.data.generation;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class LimaTagsProvider<T> extends TagsProvider<T>
{
    private final Map<TagKey<T>, LimaTagHelper<T>> helpers = new Object2ObjectOpenHashMap<>();

    protected LimaTagsProvider(PackOutput packOutput, ResourceKey<? extends Registry<T>> registryKey, String modid, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper helper)
    {
        super(packOutput, registryKey, lookupProvider, modid, helper);
    }

    protected @Nullable Registry<T> getTagRegistry()
    {
        return null;
    }

    protected ResourceKey<T> resolveKey(T value)
    {
        Registry<T> registry = Objects.requireNonNull(getTagRegistry(), "Tag provider does not support resolving keys from values.");
        return LimaRegistryUtil.getNonNullResourceKey(value, registry);
    }

    protected LimaTagHelper<T> buildTag(TagKey<T> tag)
    {
        return helpers.computeIfAbsent(tag, key -> new LimaTagHelper<>(this, key));
    }

    @SafeVarargs
    protected final void reverseTag(ResourceKey<T> resourceKey, TagKey<T>... tags)
    {
        Stream.of(tags).forEach(tag -> getOrCreateRawBuilder(tag).addElement(resourceKey.location()));
    }

    @SafeVarargs
    protected final void reverseTag(T value, TagKey<T>... tags)
    {
        reverseTag(resolveKey(value), tags);
    }

    @SafeVarargs
    protected final void reverseTag(Holder<T> holder, TagKey<T>... tags)
    {
        reverseTag(holder.value(), tags);
    }

    public static abstract class RegistryTags<T> extends LimaTagsProvider<T>
    {
        private final Registry<T> registry;

        protected RegistryTags(PackOutput packOutput, Registry<T> registry, String modid, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper helper)
        {
            super(packOutput, registry.key(), modid, lookupProvider, helper);
            this.registry = registry;
        }

        @Override
        protected final @Nullable Registry<T> getTagRegistry()
        {
            return registry;
        }
    }

    public static abstract class ItemTags extends RegistryTags<Item>
    {
        private final CompletableFuture<TagLookup<Block>> blockTagsLookup;
        private final Map<TagKey<Block>, TagKey<Item>> copyOps = new Object2ObjectOpenHashMap<>();

        protected ItemTags(PackOutput packOutput, String modid, CompletableFuture<TagLookup<Block>> blockTagsLookup, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper helper)
        {
            super(packOutput, BuiltInRegistries.ITEM, modid, lookupProvider, helper);
            this.blockTagsLookup = blockTagsLookup;
        }

        public void copyTag(TagKey<Block> blockTag, TagKey<Item> itemTag)
        {
            copyOps.put(blockTag, itemTag);
        }

        public void copyTag(TagKey<Block> blockTag)
        {
            copyTag(blockTag, TagKey.create(Registries.ITEM, blockTag.location()));
        }

        @Override
        protected CompletableFuture<HolderLookup.Provider> createContentsProvider()
        {
            return super.createContentsProvider().thenCombine(blockTagsLookup, (provider, blockTags) -> {
                copyOps.forEach((blockTag, itemTag) -> {
                    TagBuilder builder = getOrCreateRawBuilder(itemTag);
                    blockTags.apply(blockTag)
                            .orElseThrow(() -> new IllegalStateException("Can't copy non-existent block tag " + blockTag.location()))
                            .build()
                            .forEach(builder::add);
                });

                return provider;
            });
        }
    }

    public static class LimaTagHelper<T>
    {
        private final LimaTagsProvider<T> parent;
        private final TagBuilder rawBuilder;
        private final List<ResourceLocation> elements = new ObjectArrayList<>();

        public LimaTagHelper(LimaTagsProvider<T> parent, TagKey<T> tag)
        {
            this.parent = parent;
            this.rawBuilder = parent.getOrCreateRawBuilder(tag);
        }

        private LimaTagHelper<T> addInternal(ResourceLocation location)
        {
            rawBuilder.addElement(location);
            elements.add(location);
            return this;
        }

        public LimaTagHelper<T> internalBuilder(Consumer<TagBuilder> consumer)
        {
            consumer.accept(rawBuilder);
            return this;
        }

        public LimaTagHelper<T> add(ResourceKey<T> key)
        {
            return addInternal(key.location());
        }

        public LimaTagHelper<T> add(T value)
        {
            ResourceKey<T> key = parent.resolveKey(value);
            return add(key);
        }

        public LimaTagHelper<T> add(Holder<T> holder)
        {
            return add(holder.value());
        }

        @SafeVarargs
        public final LimaTagHelper<T> add(ResourceKey<T>... keys)
        {
            Stream.of(keys).map(ResourceKey::location).forEach(this::addInternal);
            return this;
        }

        @SafeVarargs
        public final LimaTagHelper<T> add(T... values)
        {
            Stream.of(values).forEach(this::add);
            return this;
        }

        @SafeVarargs
        public final LimaTagHelper<T> add(Holder<T>... holders)
        {
            Stream.of(holders).forEach(this::add);
            return this;
        }

        public LimaTagHelper<T> addValues(Collection<? extends T> values)
        {
            values.forEach(this::add);
            return this;
        }

        public LimaTagHelper<T> addHolders(Collection<Holder<T>> holders)
        {
            holders.forEach(this::add);
            return this;
        }

        public LimaTagHelper<T> addTag(TagKey<T> tag)
        {
            rawBuilder.addTag(tag.location());
            return this;
        }

        @SafeVarargs
        public final LimaTagHelper<T> addTags(TagKey<T>... tags)
        {
            Stream.of(tags).map(TagKey::location).forEach(rawBuilder::addTag);
            return this;
        }

        public LimaTagHelper<T> addOptional(ResourceLocation element)
        {
            rawBuilder.addOptionalElement(element);
            return this;
        }

        public LimaTagHelper<T> addOptional(ResourceLocation... elements)
        {
            Stream.of(elements).forEach(rawBuilder::addOptionalElement);
            return this;
        }

        public void copyTo(TagKey<T> other)
        {
            TagBuilder otherBuilder = parent.getOrCreateRawBuilder(other);
            elements.forEach(otherBuilder::addElement);
        }

        @SafeVarargs
        public final void copyTo(TagKey<T>... others)
        {
            Stream.of(others).forEach(this::copyTo);
        }
    }
}