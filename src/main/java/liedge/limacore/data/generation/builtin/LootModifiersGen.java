package liedge.limacore.data.generation.builtin;

import liedge.limacore.data.generation.loot.LimaLootModifierProvider;
import liedge.limacore.world.loot.ExtraLootTableEffectLootModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

import static liedge.limacore.LimaCore.RESOURCES;

class LootModifiersGen extends LimaLootModifierProvider
{
    LootModifiersGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(packOutput, registries, RESOURCES);
    }

    @Override
    protected void start()
    {
        add("loot_table_enchantment_component", ExtraLootTableEffectLootModifier.lootTableEffectComponentModifier());
    }
}