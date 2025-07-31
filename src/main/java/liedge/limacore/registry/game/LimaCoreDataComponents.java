package liedge.limacore.registry.game;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.LimaCore;
import liedge.limacore.network.LimaStreamCodecs;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.ExtraCodecs;
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
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY = COMPONENTS.registerComponentType(LimaCommonConstants.KEY_ENERGY_CONTAINER, builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(LimaStreamCodecs.NON_NEGATIVE_VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY_CAPACITY = COMPONENTS.registerComponentType(LimaCommonConstants.KEY_ENERGY_CAPACITY, builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(LimaStreamCodecs.NON_NEGATIVE_VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY_TRANSFER_RATE = COMPONENTS.registerComponentType(LimaCommonConstants.KEY_ENERGY_TRANSFER_RATE, builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(LimaStreamCodecs.NON_NEGATIVE_VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY_USAGE = COMPONENTS.registerComponentType(LimaCommonConstants.KEY_ENERGY_USAGE, builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(LimaStreamCodecs.NON_NEGATIVE_VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> OWNER = COMPONENTS.registerComponentType(LimaCommonConstants.KEY_OWNER, builder -> builder.persistent(UUIDUtil.CODEC).cacheEncoding());
}