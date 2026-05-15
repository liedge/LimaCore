package liedge.limacore.registry.game;

import com.mojang.serialization.MapCodec;
import liedge.limacore.LimaCore;
import liedge.limacore.world.loot.condition.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaCoreLootConditions
{
    private LimaCoreLootConditions() {}

    private static final DeferredRegister<MapCodec<? extends LootItemCondition>> CODECS = LimaCore.RESOURCES.deferredRegister(Registries.LOOT_CONDITION_TYPE);

    public static void register(IEventBus bus)
    {
        CODECS.register(bus);
    }

    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<MatchWeaponItem>> MATCH_WEAPON_ITEM = CODECS.register("match_weapon_item", () -> MatchWeaponItem.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<MatchDefaultLootTable>> MATCH_DEFAULT_LOOT_TABLE = CODECS.register("match_default_loot_table", () -> MatchDefaultLootTable.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<EntityHostilityCondition>> ENTITY_HOSTILITY = CODECS.register("entity_hostility", () -> EntityHostilityCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<DistanceCheckCondition>> DISTANCE_CHECK = CODECS.register("distance_check", () -> DistanceCheckCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<CompareValuesCondition>> COMPARE_VALUES = CODECS.register("compare_values", () -> CompareValuesCondition.CODEC);
}