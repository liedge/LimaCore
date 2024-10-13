package liedge.limacore.registry;

import com.mojang.serialization.MapCodec;
import liedge.limacore.world.loot.*;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
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
    private static final DeferredRegister<LootItemFunctionType<?>> FUNCTIONS = RESOURCES.deferredRegister(Registries.LOOT_FUNCTION_TYPE);
    private static final DeferredRegister<LootPoolEntryType> LOOT_ENTRY_TYPES = RESOURCES.deferredRegister(Registries.LOOT_POOL_ENTRY_TYPE);
    private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLM_CODECS = RESOURCES.deferredRegister(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS);

    public static void initRegister(IEventBus bus)
    {
        ENTITY_SUB_PREDICATES.register(bus);
        FUNCTIONS.register(bus);
        LOOT_ENTRY_TYPES.register(bus);
        GLM_CODECS.register(bus);
    }

    // Entity sub predicate types
    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<HostileEntitySubPredicate>> HOSTILE_ENTITY_SUB_PREDICATE = ENTITY_SUB_PREDICATES.register("hostile_entity", () -> HostileEntitySubPredicate.CODEC);

    // Functions
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<SaveBlockEntityFunction>> SAVE_BLOCK_ENTITY = FUNCTIONS.register("save_block_entity", () -> new LootItemFunctionType<>(SaveBlockEntityFunction.CODEC));

    // Loot entry types
    public static final DeferredHolder<LootPoolEntryType, LootPoolEntryType> ENCHANTMENT_BASED_WEIGHT = LOOT_ENTRY_TYPES.register("enchantment_based_weight", () -> new LootPoolEntryType(EnchantmentBasedWeightLootItem.CODEC));

    // GLM Codecs
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<AddItemLootModifier>> ADD_ITEM_MODIFIER = GLM_CODECS.register("add_item", () -> AddItemLootModifier.CODEC);
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<RemoveItemLootModifier>> REMOVE_ITEM_MODIFIER = GLM_CODECS.register("remove_item", () -> RemoveItemLootModifier.CODEC);
}