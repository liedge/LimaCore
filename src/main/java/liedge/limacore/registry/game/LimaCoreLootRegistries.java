package liedge.limacore.registry.game;

import com.mojang.serialization.MapCodec;
import liedge.limacore.advancement.EnchantmentLevelEntityPredicate;
import liedge.limacore.advancement.HostilityEntityPredicate;
import liedge.limacore.advancement.LivingHealthPredicate;
import liedge.limacore.world.loot.AddItemLootModifier;
import liedge.limacore.world.loot.DynamicWeightLootEntry;
import liedge.limacore.world.loot.RemoveItemLootModifier;
import liedge.limacore.world.loot.SaveBlockEntityFunction;
import liedge.limacore.world.loot.level.MathOpsLevelBasedValue;
import liedge.limacore.world.loot.level.RangedLookupLevelBasedValue;
import liedge.limacore.world.loot.number.*;
import net.minecraft.advancements.criterion.EntitySubPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static liedge.limacore.LimaCore.RESOURCES;

public final class LimaCoreLootRegistries
{
    private LimaCoreLootRegistries() {}

    private static final DeferredRegister<MapCodec<? extends EntitySubPredicate>> ENTITY_SUB_PREDICATES = RESOURCES.deferredRegister(Registries.ENTITY_SUB_PREDICATE_TYPE);
    private static final DeferredRegister<MapCodec<? extends LootItemFunction>> FUNCTIONS = RESOURCES.deferredRegister(Registries.LOOT_FUNCTION_TYPE);
    private static final DeferredRegister<MapCodec<? extends LootPoolEntryContainer>> LOOT_ENTRY_TYPES = RESOURCES.deferredRegister(Registries.LOOT_POOL_ENTRY_TYPE);
    private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLM_CODECS = RESOURCES.deferredRegister(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS);
    private static final DeferredRegister<MapCodec<? extends LevelBasedValue>> LBV_CODECS = RESOURCES.deferredRegister(Registries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE);
    private static final DeferredRegister<MapCodec<? extends NumberProvider>> NUMBER_PROVIDERS = RESOURCES.deferredRegister(Registries.LOOT_NUMBER_PROVIDER_TYPE);

    public static void register(IEventBus bus)
    {
        ENTITY_SUB_PREDICATES.register(bus);
        FUNCTIONS.register(bus);
        LOOT_ENTRY_TYPES.register(bus);
        GLM_CODECS.register(bus);
        LBV_CODECS.register(bus);
        NUMBER_PROVIDERS.register(bus);
    }

    // Entity sub predicate types
    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<LivingHealthPredicate>> LIVING_HEALTH_PREDICATE = ENTITY_SUB_PREDICATES.register("living_health", () -> LivingHealthPredicate.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<HostilityEntityPredicate>> HOSTILITY_ENTITY_PREDICATE = ENTITY_SUB_PREDICATES.register("hostility", () -> HostilityEntityPredicate.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<EnchantmentLevelEntityPredicate>> ENCHANTMENT_LEVEL_ENTITY_PREDICATE = ENTITY_SUB_PREDICATES.register("enchantment_level", () -> EnchantmentLevelEntityPredicate.CODEC);

    // Functions
    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<SaveBlockEntityFunction>> SAVE_BLOCK_ENTITY = FUNCTIONS.register("save_block_entity", () -> SaveBlockEntityFunction.CODEC);

    // Loot entry types
    public static final DeferredHolder<MapCodec<? extends LootPoolEntryContainer>, MapCodec<DynamicWeightLootEntry>> DYNAMIC_WEIGHT_LOOT_ENTRY = LOOT_ENTRY_TYPES.register("dynamic_weight", () -> DynamicWeightLootEntry.CODEC);

    // GLM Codecs
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<AddItemLootModifier>> ADD_ITEM_MODIFIER = GLM_CODECS.register("add_item", () -> AddItemLootModifier.CODEC);
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<RemoveItemLootModifier>> REMOVE_ITEM_MODIFIER = GLM_CODECS.register("remove_item", () -> RemoveItemLootModifier.CODEC);

    // Level based value types
    public static final DeferredHolder<MapCodec<? extends LevelBasedValue>, MapCodec<RangedLookupLevelBasedValue>> RANGED_LOOKUP_LEVEL_BASED_VALUE = LBV_CODECS.register("ranged_lookup", () -> RangedLookupLevelBasedValue.CODEC);
    public static final DeferredHolder<MapCodec<? extends LevelBasedValue>, MapCodec<MathOpsLevelBasedValue>> MATH_OPS_LEVEL_BASED_VALUE = LBV_CODECS.register("math_ops", () -> MathOpsLevelBasedValue.CODEC);
}