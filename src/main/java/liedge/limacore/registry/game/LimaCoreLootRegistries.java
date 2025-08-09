package liedge.limacore.registry.game;

import com.mojang.serialization.MapCodec;
import liedge.limacore.world.loot.*;
import liedge.limacore.world.loot.number.*;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
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
    private static final DeferredRegister<LootItemConditionType> CONDITIONS = RESOURCES.deferredRegister(Registries.LOOT_CONDITION_TYPE);
    private static final DeferredRegister<LootItemFunctionType<?>> FUNCTIONS = RESOURCES.deferredRegister(Registries.LOOT_FUNCTION_TYPE);
    private static final DeferredRegister<LootPoolEntryType> LOOT_ENTRY_TYPES = RESOURCES.deferredRegister(Registries.LOOT_POOL_ENTRY_TYPE);
    private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLM_CODECS = RESOURCES.deferredRegister(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS);
    private static final DeferredRegister<MapCodec<? extends LevelBasedValue>> LBV_CODECS = RESOURCES.deferredRegister(Registries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE);
    private static final DeferredRegister<LootNumberProviderType> NUMBER_PROVIDERS = RESOURCES.deferredRegister(Registries.LOOT_NUMBER_PROVIDER_TYPE);

    public static void register(IEventBus bus)
    {
        ENTITY_SUB_PREDICATES.register(bus);
        CONDITIONS.register(bus);
        FUNCTIONS.register(bus);
        LOOT_ENTRY_TYPES.register(bus);
        GLM_CODECS.register(bus);
        LBV_CODECS.register(bus);
        NUMBER_PROVIDERS.register(bus);
    }

    // Entity sub predicate types
    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<HostilityEntityPredicate>> HOSTILITY_ENTITY_PREDICATE = ENTITY_SUB_PREDICATES.register("hostility", () -> HostilityEntityPredicate.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<EnchantmentLevelEntityPredicate>> ENCHANTMENT_LEVEL_ENTITY_PREDICATE = ENTITY_SUB_PREDICATES.register("enchantment_level", () -> EnchantmentLevelEntityPredicate.CODEC);

    // Conditions
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> HOSTILITY_CONDITION = CONDITIONS.register("hostility", () -> new LootItemConditionType(EntityHostilityLootCondition.CODEC));

    // Functions
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<SaveBlockEntityFunction>> SAVE_BLOCK_ENTITY = FUNCTIONS.register("save_block_entity", () -> new LootItemFunctionType<>(SaveBlockEntityFunction.CODEC));

    // Loot entry types
    public static final DeferredHolder<LootPoolEntryType, LootPoolEntryType> DYNAMIC_WEIGHT_LOOT_ENTRY = LOOT_ENTRY_TYPES.register("dynamic_weight", () -> new LootPoolEntryType(DynamicWeightLootEntry.CODEC));

    // GLM Codecs
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<AddItemLootModifier>> ADD_ITEM_MODIFIER = GLM_CODECS.register("add_item", () -> AddItemLootModifier.CODEC);
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<RemoveItemLootModifier>> REMOVE_ITEM_MODIFIER = GLM_CODECS.register("remove_item", () -> RemoveItemLootModifier.CODEC);

    // Level based value types
    public static final DeferredHolder<MapCodec<? extends LevelBasedValue>, MapCodec<EnhancedLookupLevelBasedValue>> ENHANCED_LOOKUP_LEVEL_BASED_VALUE = LBV_CODECS.register("enhanced_lookup", () -> EnhancedLookupLevelBasedValue.CODEC);
    public static final DeferredHolder<MapCodec<? extends LevelBasedValue>, MapCodec<MathOpsLevelBasedValue>> MATH_OPS_LEVEL_BASED_VALUE = LBV_CODECS.register("math_ops", () -> MathOpsLevelBasedValue.CODEC);

    // Loot number types
    public static final DeferredHolder<LootNumberProviderType, LootNumberProviderType> DISTANCE_NUMBER_PROVIDER = NUMBER_PROVIDERS.register("distance", () -> new LootNumberProviderType(DistanceNumberProvider.CODEC));
    public static final DeferredHolder<LootNumberProviderType, LootNumberProviderType> ROUNDING_NUMBER_PROVIDER = NUMBER_PROVIDERS.register("rounding", () -> new LootNumberProviderType(RoundingNumberProvider.CODEC));
    public static final DeferredHolder<LootNumberProviderType, LootNumberProviderType> TARGETED_ATTRIBUTE_VALUE_NUMBER_PROVIDER = NUMBER_PROVIDERS.register("targeted_attribute_value", () -> new LootNumberProviderType(TargetedAttributeValueProvider.CODEC));
    public static final DeferredHolder<LootNumberProviderType, LootNumberProviderType> TARGETED_ENCHANTMENT_LEVEL_NUMBER_PROVIDER = NUMBER_PROVIDERS.register("targeted_enchantment_level", () -> new LootNumberProviderType(TargetedEnchantmentLevelProvider.CODEC));
    public static final DeferredHolder<LootNumberProviderType, LootNumberProviderType> MATH_OPS_NUMBER_PROVIDER = NUMBER_PROVIDERS.register("math_ops", () -> new LootNumberProviderType(MathOpsNumberProvider.CODEC));
}