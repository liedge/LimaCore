package liedge.limacore.registry.game;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.LimaCore;
import liedge.limacore.capability.energy.EnergyContainerSpec;
import liedge.limacore.network.LimaStreamCodecs;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;

public final class LimaCoreDataComponents
{
    private LimaCoreDataComponents() {}

    private static final DeferredRegister.DataComponents COMPONENTS = LimaCore.RESOURCES.deferredDataComponents();

    public static void register(IEventBus bus)
    {
        COMPONENTS.register(bus);
    }

    // Standard data components
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> ITEM_CONTAINER = COMPONENTS.registerComponentType(LimaCommonConstants.KEY_ITEM_CONTAINER, builder -> builder.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY = COMPONENTS.registerComponentType(LimaCommonConstants.KEY_ENERGY_CONTAINER, builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(LimaStreamCodecs.varIntRange(0, Integer.MAX_VALUE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EnergyContainerSpec>> ENERGY_SPEC = COMPONENTS.registerComponentType(LimaCommonConstants.KEY_ENERGY_CONTAINER_SPEC, builder -> builder.persistent(EnergyContainerSpec.CODEC).networkSynchronized(EnergyContainerSpec.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> OWNER = COMPONENTS.registerComponentType(LimaCommonConstants.KEY_OWNER, builder -> builder.persistent(UUIDUtil.CODEC).cacheEncoding());
}