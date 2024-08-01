package liedge.limacore.data.generation.loot;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import liedge.limacore.util.LimaCollectionsUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Map;
import java.util.function.BiConsumer;

public abstract class LimaLootSubProvider implements LimaLootSubProviderExtensions
{
    private final Map<ResourceKey<LootTable>, LootTable.Builder> tables = new Object2ObjectOpenHashMap<>();
    private final HolderLookup.Provider registries;

    protected LimaLootSubProvider(HolderLookup.Provider registries)
    {
        this.registries = registries;
    }

    protected void addTable(ResourceKey<LootTable> key, LootTable.Builder builder)
    {
        LimaCollectionsUtil.putNoDuplicates(tables, key, builder);
    }

    protected abstract void generateTables(HolderLookup.Provider registries);

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output)
    {
        generateTables(registries);
        tables.forEach(output);
    }
}