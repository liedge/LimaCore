package liedge.limacore.registry.game;

import com.mojang.serialization.MapCodec;
import liedge.limacore.advancement.EnchantmentLevelEntityPredicate;
import liedge.limacore.advancement.HostilityEntityPredicate;
import liedge.limacore.advancement.InvertedEntitySubPredicate;
import liedge.limacore.advancement.LivingHealthPredicate;
import liedge.limacore.lib.ModResources;
import liedge.limacore.world.loot.ApplyFunctionsLootModifier;
import liedge.limacore.world.loot.DynamicWeightLootEntry;
import liedge.limacore.world.loot.RemoveItemLootModifier;
import liedge.limacore.world.loot.SaveBlockEntityFunction;
import liedge.limacore.world.loot.condition.*;
import liedge.limacore.world.loot.level.MathOpsLevelBasedValue;
import liedge.limacore.world.loot.level.RangedLookupLevelBasedValue;
import liedge.limacore.world.loot.number.*;
import net.minecraft.advancements.criterion.EntitySubPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.ApiStatus;

public final class LimaCoreLootRegistries
{
    private LimaCoreLootRegistries() {}

    @ApiStatus.Internal
    public static void register(RegisterEvent event, ModResources resources)
    {
        resources.registerByEvent(Registries.LOOT_CONDITION_TYPE, event, LimaCoreLootRegistries::registerConditions);
        resources.registerByEvent(Registries.LOOT_FUNCTION_TYPE, event, LimaCoreLootRegistries::registerFunctions);
        resources.registerByEvent(Registries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE, event, LimaCoreLootRegistries::registerLevelBasedValues);
        resources.registerByEvent(Registries.LOOT_NUMBER_PROVIDER_TYPE, event, LimaCoreLootRegistries::registerLootNumbers);
        resources.registerByEvent(Registries.ENTITY_SUB_PREDICATE_TYPE, event, LimaCoreLootRegistries::registerSubPredicates);
        resources.registerByEvent(Registries.LOOT_POOL_ENTRY_TYPE, event, LimaCoreLootRegistries::registerLootEntryTypes);
        resources.registerByEvent(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, event, LimaCoreLootRegistries::registerGLMCodecs);
    }

    private static void registerConditions(ModResources.RegisterHelper<MapCodec<? extends LootItemCondition>> helper)
    {
        helper.register("match_weapon_item", MatchWeaponItem.CODEC);
        helper.register("match_default_loot_table", MatchDefaultLootTable.CODEC);
        helper.register("entity_hostility", EntityHostilityCondition.CODEC);
        helper.register("distance_check", DistanceCheckCondition.CODEC);
        helper.register("compare_values", CompareValuesCondition.CODEC);
    }

    private static void registerFunctions(ModResources.RegisterHelper<MapCodec<? extends LootItemFunction>> helper)
    {
        helper.register("save_block_entity", SaveBlockEntityFunction.CODEC);
    }

    private static void registerLevelBasedValues(ModResources.RegisterHelper<MapCodec<? extends LevelBasedValue>> helper)
    {
        helper.register("ranged_lookup", RangedLookupLevelBasedValue.CODEC);
        helper.register("math_ops", MathOpsLevelBasedValue.CODEC);
    }

    private static void registerLootNumbers(ModResources.RegisterHelper<MapCodec<? extends NumberProvider>> helper)
    {
        helper.register("distance", LootContextDistance.CODEC);
        helper.register("rounding", RoundValue.CODEC);
        helper.register("entity_attribute", EntityAttributeValue.CODEC);
        helper.register("enchantment_level", EntityEnchantmentLevels.CODEC);
        helper.register("math", ValueMathOperation.CODEC);
    }

    private static void registerSubPredicates(ModResources.RegisterHelper<MapCodec<? extends EntitySubPredicate>> helper)
    {
        helper.register("not", InvertedEntitySubPredicate.CODEC);
        helper.register("living_health", LivingHealthPredicate.CODEC);
        helper.register("hostility", HostilityEntityPredicate.CODEC);
        helper.register("enchantment_level", EnchantmentLevelEntityPredicate.CODEC);
    }

    private static void registerLootEntryTypes(ModResources.RegisterHelper<MapCodec<? extends LootPoolEntryContainer>> helper)
    {
        helper.register("dynamic_weight", DynamicWeightLootEntry.CODEC);
    }

    private static void registerGLMCodecs(ModResources.RegisterHelper<MapCodec<? extends IGlobalLootModifier>> helper)
    {
        helper.register("remove_item", RemoveItemLootModifier.CODEC);
        helper.register("apply_functions", ApplyFunctionsLootModifier.CODEC);
    }
}