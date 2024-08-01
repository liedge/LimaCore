package liedge.limacore.data.generation.loot;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class LimaLootTableProvider extends LootTableProvider
{
    private final List<SubProviderEntry> subProviders = new ObjectArrayList<>();

    protected LimaLootTableProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(packOutput, Set.of(), List.of(), registries);
    }

    protected abstract void createSubProviders();

    protected void addSubProvider(Function<HolderLookup.Provider, ? extends LootTableSubProvider> function, LootContextParamSet paramSet)
    {
        subProviders.add(new SubProviderEntry(function::apply, paramSet));
    }

    @Override
    public List<SubProviderEntry> getTables()
    {
        if (subProviders.isEmpty())
            createSubProviders();

        return subProviders;
    }

    @Override
    protected void validate(WritableRegistry<LootTable> registry, ValidationContext validationContext, ProblemReporter.Collector reporter) { }
}