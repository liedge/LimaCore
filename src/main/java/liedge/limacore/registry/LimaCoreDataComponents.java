package liedge.limacore.registry;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.LimaCore;
import liedge.limacore.network.LimaStreamCodecs;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaCoreDataComponents
{
    private LimaCoreDataComponents() {}

    private static final DeferredRegister<DataComponentType<?>> TYPES = LimaCore.RESOURCES.deferredRegister(Registries.DATA_COMPONENT_TYPE);

    public static void initRegister(IEventBus bus)
    {
        TYPES.register(bus);
    }

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> ITEM_CONTAINER = TYPES.register(LimaCommonConstants.KEY_ITEM_CONTAINER, () -> DataComponentType.<ItemContainerContents>builder()
            .persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY = TYPES.register(LimaCommonConstants.KEY_ENERGY_CONTAINER, () -> DataComponentType.<Integer>builder()
            .persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(LimaStreamCodecs.NON_NEGATIVE_VAR_INT).build());
}