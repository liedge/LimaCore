package liedge.limacore.advancement;

import liedge.limacore.lib.ModResources;
import liedge.limacore.registry.game.LimaCoreTriggerTypes;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class LimaAdvancementUtil
{
    private LimaAdvancementUtil() {}

    public static EntityPredicate.Builder matchesEntityType(Holder<EntityType<?>> holder)
    {
        return EntityPredicate.Builder.entity().entityType(new EntityTypePredicate(HolderSet.direct(holder)));
    }

    public static EntityPredicate.Builder matchesEntityType(EntityType<?> type)
    {
        return matchesEntityType(LimaRegistryUtil.builtInHolder(type));
    }

    public static ItemPredicate matchingItems(ItemLike... items)
    {
        HolderSet<Item> set = HolderSet.direct(itemLike -> LimaRegistryUtil.builtInHolder(itemLike.asItem()), items);
        return new ItemPredicate(Optional.of(set), MinMaxBounds.Ints.ANY, DataComponentMatchers.ANY);
    }

    public static Criterion<PlayerTrigger.TriggerInstance> playerLoggedIn(@Nullable EntityPredicate.Builder playerPredicate)
    {
        Optional<ContextAwarePredicate> player = Optional.ofNullable(playerPredicate).map(EntityPredicate::wrap);
        return LimaCoreTriggerTypes.PLAYER_LOGGED_IN.get().createCriterion(new PlayerTrigger.TriggerInstance(player));
    }

    public static Criterion<PlayerTrigger.TriggerInstance> playerLoggedIn()
    {
        return playerLoggedIn(null);
    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> playerHasTagItems(HolderGetter<Item> holders, TagKey<Item> tagKey)
    {
        ItemPredicate.Builder predicate = ItemPredicate.Builder.item().of(holders, tagKey);
        return InventoryChangeTrigger.TriggerInstance.hasItems(predicate);
    }

    public static String defaultAdvancementTitleKey(Identifier id)
    {
        return ModResources.prefixedIdLangKey("advancement", id);
    }

    public static String defaultAdvancementDescriptionKey(Identifier id)
    {
        return ModResources.prefixedVariantIdLangKey("advancement", "desc", id);
    }

    public static ResourceKey<LootTable> defaultAdvancementLootTable(Identifier id)
    {
        return ResourceKey.create(Registries.LOOT_TABLE, id.withPrefix("advancement/"));
    }
}