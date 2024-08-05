package liedge.limacore.registry;

import com.mojang.serialization.MapCodec;
import liedge.limacore.LimaCore;
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

public final class LimaCoreLootRegistries
{
    private LimaCoreLootRegistries() {}

    private static final DeferredRegister<MapCodec<? extends EntitySubPredicate>> SUB_PREDICATE_CODECS = LimaCore.RESOURCES.deferredRegister(Registries.ENTITY_SUB_PREDICATE_TYPE);
    private static final DeferredRegister<LootItemFunctionType<?>> FUNCTIONS = LimaCore.RESOURCES.deferredRegister(Registries.LOOT_FUNCTION_TYPE);
    private static final DeferredRegister<LootPoolEntryType> LOOT_ENTRY_TYPES = LimaCore.RESOURCES.deferredRegister(Registries.LOOT_POOL_ENTRY_TYPE);
    private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLM_CODECS = LimaCore.RESOURCES.deferredRegister(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS);

    public static void initRegister(IEventBus bus)
    {
        SUB_PREDICATE_CODECS.register(bus);
        FUNCTIONS.register(bus);
        LOOT_ENTRY_TYPES.register(bus);
        GLM_CODECS.register(bus);
    }

    // Entity sub predicate types
    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<HostileEntitySubPredicate>> HOSTILE_ENTITY_SUB_PREDICATE = SUB_PREDICATE_CODECS.register("hostile_entity", () -> HostileEntitySubPredicate.CODEC);

    // Functions
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<SaveBlockEntityFunction>> SAVE_BLOCK_ENTITY = FUNCTIONS.register("save_block_entity", () -> new LootItemFunctionType<>(SaveBlockEntityFunction.MAP_CODEC));

    // Loot entry types
    public static final DeferredHolder<LootPoolEntryType, LootPoolEntryType> ENCHANTMENT_BASED_WEIGHT = LOOT_ENTRY_TYPES.register("enchantment_based_weight", () -> new LootPoolEntryType(EnchantmentBasedWeightLootItem.CODEC));

    // GLM Codecs
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<AddItemLootModifier>> ADD_ITEM_MODIFIER = GLM_CODECS.register("add_item", () -> AddItemLootModifier.MODIFIER_CODEC);
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<RemoveItemLootModifier>> REMOVE_ITEM_MODIFIER = GLM_CODECS.register("remove_item", () -> RemoveItemLootModifier.MODIFIER_CODEC);
}