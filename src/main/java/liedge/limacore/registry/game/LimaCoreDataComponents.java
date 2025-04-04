package liedge.limacore.registry.game;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.LimaCore;
import liedge.limacore.capability.energy.EnergyContainerSpec;
import liedge.limacore.network.LimaStreamCodecs;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.UUID;

public final class LimaCoreDataComponents
{
    private LimaCoreDataComponents() {}

    private static final DeferredRegister.DataComponents COMPONENTS = LimaCore.RESOURCES.deferredDataComponents();
    private static final DeferredRegister.DataComponents ENCHANTMENT_COMPONENTS = LimaCore.RESOURCES.deferredDataComponents(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE);

    public static void register(IEventBus bus)
    {
        COMPONENTS.register(bus);
        ENCHANTMENT_COMPONENTS.register(bus);
    }

    // Standard data components
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> ITEM_CONTAINER = COMPONENTS.registerComponentType(LimaCommonConstants.KEY_ITEM_CONTAINER, builder -> builder.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY = COMPONENTS.registerComponentType(LimaCommonConstants.KEY_ENERGY_CONTAINER, builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(LimaStreamCodecs.varIntRange(0, Integer.MAX_VALUE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EnergyContainerSpec>> ENERGY_SPEC = COMPONENTS.registerComponentType(LimaCommonConstants.KEY_ENERGY_CONTAINER_SPEC, builder -> builder.persistent(EnergyContainerSpec.CODEC).networkSynchronized(EnergyContainerSpec.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> OWNER = COMPONENTS.registerComponentType(LimaCommonConstants.KEY_OWNER, builder -> builder.persistent(UUIDUtil.CODEC).cacheEncoding());

    // Enchantment data components
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<ResourceKey<LootTable>>>>> EXTRA_LOOT_TABLE_EFFECT = ENCHANTMENT_COMPONENTS.registerComponentType("extra_loot_table", builder -> builder.persistent(ConditionalEffect.codec(ResourceKey.codec(Registries.LOOT_TABLE), LootContextParamSets.ENCHANTED_DAMAGE).listOf()));
}