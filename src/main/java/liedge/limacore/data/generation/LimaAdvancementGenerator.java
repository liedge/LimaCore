package liedge.limacore.data.generation;

import liedge.limacore.advancement.LimaAdvancementUtil;
import net.minecraft.advancements.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class LimaAdvancementGenerator implements AdvancementProvider.AdvancementGenerator
{
    private final ExistingFileHelper helper;

    protected LimaAdvancementGenerator(ExistingFileHelper helper)
    {
        this.helper = helper;
    }

    public AdvancementProvider buildProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
    {
        return new AdvancementProvider(output, registries, helper, List.of(this));
    }

    protected BuilderHelper getBuilder(ResourceLocation id)
    {
        return new BuilderHelper(id);
    }

    protected static class BuilderHelper
    {
        private final ResourceLocation id;
        private final Advancement.Builder builder = Advancement.Builder.advancement();

        private ItemLike iconItem;
        private AdvancementType advancementType = AdvancementType.TASK;
        private ResourceLocation background;
        private boolean showToast = true;
        private boolean announceToChat = true;
        private boolean hidden = false;
        private Component title;
        private Component description;

        private BuilderHelper(ResourceLocation id)
        {
            this.id = id;
        }

        public AdvancementHolder build(Consumer<AdvancementHolder> saver, ExistingFileHelper helper)
        {
            ItemStack icon = new ItemStack(Objects.requireNonNull(iconItem, "No icon item for advancement " + id));
            this.defaultTitle().defaultDescription().builder.display(new DisplayInfo(icon, title, description, Optional.ofNullable(background), advancementType, showToast, announceToChat, hidden));
            return builder.save(saver, id, helper);
        }

        public BuilderHelper parent(AdvancementHolder holder)
        {
            builder.parent(holder);
            return this;
        }

        public BuilderHelper icon(ItemLike iconItem)
        {
            this.iconItem = iconItem;
            return this;
        }

        public BuilderHelper defaultTitle(Object... args)
        {
            if (title == null)
            {
                String key = LimaAdvancementUtil.defaultAdvancementTitle(id);
                title = Component.translatable(key, args);
            }

            return this;
        }

        public BuilderHelper defaultDescription(Object... args)
        {
            if (description == null)
            {
                String key = LimaAdvancementUtil.defaultAdvancementDescription(id);
                description = Component.translatable(key, args);
            }

            return this;
        }

        public BuilderHelper customTitle(Component title)
        {
            this.title = title;
            return this;
        }

        public BuilderHelper customDescription(Component description)
        {
            this.description = description;
            return this;
        }

        public BuilderHelper setAdvancementType(AdvancementType advancementType)
        {
            this.advancementType = advancementType;
            return this;
        }

        public BuilderHelper visibility(boolean showToast, boolean announceToChat, boolean hidden)
        {
            this.showToast = showToast;
            this.announceToChat = announceToChat;
            this.hidden = hidden;
            return this;
        }

        public BuilderHelper rootAdvancement(ItemLike iconItem, ResourceLocation background)
        {
            this.background = background;
            return icon(iconItem).setAdvancementType(AdvancementType.TASK).visibility(false, false, false);
        }

        public BuilderHelper criterion(String criterionKey, Criterion<?> criterion)
        {
            builder.addCriterion(criterionKey, criterion);
            return this;
        }

        public BuilderHelper criterionStrategy(AdvancementRequirements.Strategy strategy)
        {
            builder.requirements(strategy);
            return this;
        }

        public BuilderHelper criterionStrategy(AdvancementRequirements requirements)
        {
            builder.requirements(requirements);
            return this;
        }

        public BuilderHelper rewards(AdvancementRewards.Builder rewardsBuilder)
        {
            builder.rewards(rewardsBuilder);
            return this;
        }

        public BuilderHelper accessBuilder(Consumer<Advancement.Builder> consumer)
        {
            consumer.accept(builder);
            return this;
        }
    }
}