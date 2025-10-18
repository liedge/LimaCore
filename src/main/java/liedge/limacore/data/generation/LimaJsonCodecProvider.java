package liedge.limacore.data.generation;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.lib.ModResources;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.WithConditions;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.JsonCodecProvider;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class LimaJsonCodecProvider<T> extends JsonCodecProvider<T>
{
    protected final ModResources resources;

    protected LimaJsonCodecProvider(PackOutput packOutput,
                                    PackOutput.Target target,
                                    String directory,
                                    PackType packType,
                                    Codec<T> codec,
                                    CompletableFuture<HolderLookup.Provider> registries,
                                    ModResources resources,
                                    ExistingFileHelper helper)
    {
        super(packOutput, target, directory, packType, codec, registries, resources.modid(), helper);
        this.resources = resources;
    }

    protected abstract void gather(HolderLookup.Provider registries);

    @Deprecated
    @Override
    protected final void gather() {}

    @Override
    public final CompletableFuture<?> run(CachedOutput cache)
    {
        return lookupProvider.thenCompose(registries -> runAsync(cache, registries));
    }

    private CompletableFuture<?> runAsync(CachedOutput cache, HolderLookup.Provider registries)
    {
        List<CompletableFuture<?>> futures = new ObjectArrayList<>();
        DynamicOps<JsonElement> ops = new ConditionalOps<>(registries.createSerializationContext(JsonOps.INSTANCE), ICondition.IContext.EMPTY);
        Codec<Optional<WithConditions<T>>> wrappedCodec = ConditionalOps.createConditionalCodecWithConditions(codec);
        gather(registries);

        for (var entry : conditions.entrySet())
        {
            Path path = pathProvider.json(entry.getKey());

            CompletableFuture<?> future = CompletableFuture
                    .supplyAsync(() -> LimaCoreCodecs.strictEncode(wrappedCodec, ops, Optional.of(entry.getValue())))
                    .thenComposeAsync(json -> DataProvider.saveStable(cache, json, path));

            futures.add(future);
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    protected void unconditional(String name, T value)
    {
        unconditional(resources.location(name), value);
    }

    protected void conditionally(String name, Consumer<WithConditions.Builder<T>> configurator)
    {
        conditionally(resources.location(name), configurator);
    }
}