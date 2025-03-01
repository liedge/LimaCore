package liedge.limacore.registry;

import com.mojang.serialization.Codec;
import liedge.limacore.LimaCommonConstants;
import liedge.limacore.LimaCore;
import liedge.limacore.network.LimaStreamCodecs;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public final class LimaCoreDataComponents
{
    private LimaCoreDataComponents() {}

    private static final DeferredRegister.DataComponents COMPONENTS = LimaCore.RESOURCES.deferredDataComponents();
    private static final DeferredRegister.DataComponents ENCHANTMENT_COMPONENTS = LimaCore.RESOURCES.deferredDataComponents(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE);

    public static void initRegister(IEventBus bus)
    {
        COMPONENTS.register(bus);
        ENCHANTMENT_COMPONENTS.register(bus);
    }

    // Standard data components
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> ITEM_CONTAINER = COMPONENTS.registerComponentType(LimaCommonConstants.KEY_ITEM_CONTAINER, builder -> builder.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY = COMPONENTS.registerComponentType(LimaCommonConstants.KEY_ENERGY_CONTAINER, builder -> builder.persistent(Codec.intRange(0, Integer.MAX_VALUE)).networkSynchronized(LimaStreamCodecs.varIntRange(0, Integer.MAX_VALUE)));

    // Enchantment data components
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<ResourceKey<LootTable>>>>> EXTRA_LOOT_TABLE_EFFECT = ENCHANTMENT_COMPONENTS.registerComponentType("extra_loot_table", builder -> builder.persistent(ConditionalEffect.codec(ResourceKey.codec(Registries.LOOT_TABLE), LootContextParamSets.ENCHANTED_DAMAGE).listOf()));
}