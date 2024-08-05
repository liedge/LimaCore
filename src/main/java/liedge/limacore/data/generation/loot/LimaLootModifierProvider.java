package liedge.limacore.data.generation.loot;

import liedge.limacore.lib.ModResources;
import liedge.limacore.world.loot.LootModifierBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class LimaLootModifierProvider extends GlobalLootModifierProvider
{
    protected LimaLootModifierProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, ModResources resources)
    {
        super(packOutput, registries, resources.modid());
    }

    protected <T extends IGlobalLootModifier> void add(String name, T modifier)
    {
        add(name, modifier, List.of());
    }

    protected void add(String name, LootModifierBuilder<?, ?> builder)
    {
        add(name, builder.build());
    }
}