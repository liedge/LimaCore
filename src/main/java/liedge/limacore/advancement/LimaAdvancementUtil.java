package liedge.limacore.advancement;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.ModResources;
import liedge.limacore.registry.LimaCoreTriggerTypes;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class LimaAdvancementUtil
{
    private LimaAdvancementUtil() {}

    public static Criterion<PlayerTrigger.TriggerInstance> playerLoggedIn()
    {
        return LimaCoreTriggerTypes.PLAYER_LOGGED_IN.get().createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
    }

    public static <T extends SimpleCriterionTrigger.SimpleInstance> Products.P1<RecordCodecBuilder.Mu<T>, Optional<ContextAwarePredicate>> playerCodecStart(RecordCodecBuilder.Instance<T> instance)
    {
        return instance.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(SimpleCriterionTrigger.SimpleInstance::player));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static boolean testEntityPredicate(Optional<ContextAwarePredicate> optional, ServerPlayer serverPlayer, Entity toTest)
    {
        return optional.map(predicate -> testEntityPredicate(predicate, serverPlayer, toTest)).orElse(true);
    }

    public static boolean testEntityPredicate(ContextAwarePredicate predicate, ServerPlayer serverPlayer, Entity toTest)
    {
        return predicate.matches(EntityPredicate.createContext(serverPlayer, toTest));
    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> playerHasItem(Supplier<? extends ItemLike> supplier)
    {
        return InventoryChangeTrigger.TriggerInstance.hasItems(supplier.get());
    }

    @SafeVarargs
    public static Criterion<InventoryChangeTrigger.TriggerInstance> playerHasItems(Supplier<? extends ItemLike>... suppliers)
    {
        ItemLike[] items = Stream.of(suppliers).map(Supplier::get).toArray(ItemLike[]::new);
        return InventoryChangeTrigger.TriggerInstance.hasItems(items);
    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> playerHasItems(TagKey<Item> tagKey)
    {
        ItemPredicate predicate = ItemPredicate.Builder.item().of(tagKey).build();
        return InventoryChangeTrigger.TriggerInstance.hasItems(predicate);
    }

    public static String defaultAdvancementTitle(ResourceLocation id)
    {
        return ModResources.prefixSuffixIdTranslationKey("advancement", "title", id);
    }

    public static String defaultAdvancementDescription(ResourceLocation id)
    {
        return ModResources.prefixSuffixIdTranslationKey("advancement", "description", id);
    }

    public static ResourceKey<LootTable> defaultAdvancementLootTable(ResourceLocation id)
    {
        return ResourceKey.create(Registries.LOOT_TABLE, id.withPrefix("advancement/"));
    }
}