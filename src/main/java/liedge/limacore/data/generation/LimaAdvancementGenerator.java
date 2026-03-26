package liedge.limacore.data.generation;

import liedge.limacore.advancement.LimaAdvancementUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static liedge.limacore.advancement.LimaAdvancementUtil.defaultAdvancementDescriptionKey;
import static liedge.limacore.advancement.LimaAdvancementUtil.defaultAdvancementTitleKey;

public abstract class LimaAdvancementGenerator implements AdvancementSubProvider
{
    public static AdvancementProvider createDataProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, Supplier<? extends LimaAdvancementGenerator> supplier)
    {
        LimaAdvancementGenerator generator = supplier.get();
        return new AdvancementProvider(packOutput, registries, List.of(generator));
    }

    protected Component defaultTitle(Identifier id)
    {
        return Component.translatable(defaultAdvancementTitleKey(id));
    }

    protected Component defaultDesc(Identifier id)
    {
        return Component.translatable(defaultAdvancementDescriptionKey(id));
    }

    protected Advancement.Builder builder(Identifier id, ItemStackTemplate icon, AdvancementType type, @Nullable Identifier background, boolean showToast, boolean announceToChat, boolean hidden)
    {
        return Advancement.Builder.advancement().display(icon, defaultTitle(id), defaultDesc(id), background, type, showToast, announceToChat, hidden);
    }

    protected Advancement.Builder builder(Identifier id, ItemLike iconItem, AdvancementType type, @Nullable Identifier background, boolean showToast, boolean announceToChat, boolean hidden)
    {
        return builder(id, new ItemStackTemplate(iconItem.asItem()), type, background, showToast, announceToChat, hidden);
    }

    protected Advancement.Builder rootBuilder(Identifier id, ItemStackTemplate icon, AdvancementType type, Identifier background)
    {
        return builder(id, icon, type, background, false, false, false);
    }

    protected Advancement.Builder rootBuilder(Identifier id, ItemLike iconItem, AdvancementType type, Identifier background)
    {
        return rootBuilder(id, new ItemStackTemplate(iconItem.asItem()), type, background);
    }

    protected Advancement.Builder normalBuilder(Identifier id, ItemStackTemplate icon, AdvancementType type)
    {
        return builder(id, icon, type, null, true, true, false);
    }

    protected Advancement.Builder normalBuilder(Identifier id, ItemLike iconItem, AdvancementType type)
    {
        return normalBuilder(id, new ItemStackTemplate(iconItem.asItem()), type);
    }

    protected AdvancementRewards.Builder defaultLootReward(Identifier id)
    {
        return AdvancementRewards.Builder.loot(LimaAdvancementUtil.defaultAdvancementLootTable(id));
    }
}