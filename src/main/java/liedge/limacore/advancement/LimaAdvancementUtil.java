package liedge.limacore.advancement;

import liedge.limacore.lib.ModResources;
import liedge.limacore.registry.game.LimaCoreTriggerTypes;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class LimaAdvancementUtil
{
    private LimaAdvancementUtil() {}

    public static Criterion<PlayerTrigger.TriggerInstance> playerLoggedIn(@Nullable EntityPredicate.Builder playerPredicate)
    {
        Optional<ContextAwarePredicate> player = Optional.ofNullable(playerPredicate).map(EntityPredicate::wrap);
        return LimaCoreTriggerTypes.PLAYER_LOGGED_IN.get().createCriterion(new PlayerTrigger.TriggerInstance(player));
    }

    public static Criterion<PlayerTrigger.TriggerInstance> playerLoggedIn()
    {
        return playerLoggedIn(null);
    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> playerHasItems(TagKey<Item> tagKey)
    {
        ItemPredicate predicate = ItemPredicate.Builder.item().of(tagKey).build();
        return InventoryChangeTrigger.TriggerInstance.hasItems(predicate);
    }

    public static String defaultAdvancementTitleKey(ResourceLocation id)
    {
        return ModResources.prefixedIdLangKey("advancement", id);
    }

    public static String defaultAdvancementDescriptionKey(ResourceLocation id)
    {
        return ModResources.prefixedVariantIdLangKey("advancement", "desc", id);
    }

    public static ResourceKey<LootTable> defaultAdvancementLootTable(ResourceLocation id)
    {
        return ResourceKey.create(Registries.LOOT_TABLE, id.withPrefix("advancement/"));
    }
}