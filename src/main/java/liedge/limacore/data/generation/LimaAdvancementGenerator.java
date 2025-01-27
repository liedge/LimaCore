package liedge.limacore.data.generation;

import liedge.limacore.advancement.LimaAdvancementUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static liedge.limacore.advancement.LimaAdvancementUtil.defaultAdvancementDescriptionKey;
import static liedge.limacore.advancement.LimaAdvancementUtil.defaultAdvancementTitleKey;

public abstract class LimaAdvancementGenerator implements AdvancementProvider.AdvancementGenerator
{
    public static AdvancementProvider createDataProvider(PackOutput packOutput, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider> registries, Supplier<? extends LimaAdvancementGenerator> supplier)
    {
        LimaAdvancementGenerator generator = supplier.get();
        return new AdvancementProvider(packOutput, registries, helper, List.of(generator));
    }

    protected Component defaultTitle(ResourceLocation id)
    {
        return Component.translatable(defaultAdvancementTitleKey(id));
    }

    protected Component defaultDesc(ResourceLocation id)
    {
        return Component.translatable(defaultAdvancementDescriptionKey(id));
    }

    protected Advancement.Builder builder(ResourceLocation id, ItemStack icon, AdvancementType type, @Nullable ResourceLocation background, boolean showToast, boolean announceToChat, boolean hidden)
    {
        return Advancement.Builder.advancement().display(icon, defaultTitle(id), defaultDesc(id), background, type, showToast, announceToChat, hidden);
    }

    protected Advancement.Builder builder(ResourceLocation id, ItemLike iconItem, AdvancementType type, @Nullable ResourceLocation background, boolean showToast, boolean announceToChat, boolean hidden)
    {
        return builder(id, new ItemStack(iconItem.asItem()), type, background, showToast, announceToChat, hidden);
    }

    protected Advancement.Builder rootBuilder(ResourceLocation id, ItemStack icon, AdvancementType type, ResourceLocation background)
    {
        return builder(id, icon, type, background, false, false, false);
    }

    protected Advancement.Builder rootBuilder(ResourceLocation id, ItemLike iconItem, AdvancementType type, ResourceLocation background)
    {
        return rootBuilder(id, new ItemStack(iconItem.asItem()), type, background);
    }

    protected Advancement.Builder normalBuilder(ResourceLocation id, ItemStack icon, AdvancementType type)
    {
        return builder(id, icon, type, null, true, true, false);
    }

    protected Advancement.Builder normalBuilder(ResourceLocation id, ItemLike iconItem, AdvancementType type)
    {
        return normalBuilder(id, new ItemStack(iconItem.asItem()), type);
    }

    protected AdvancementRewards.Builder defaultLootReward(ResourceLocation id)
    {
        return AdvancementRewards.Builder.loot(LimaAdvancementUtil.defaultAdvancementLootTable(id));
    }
}