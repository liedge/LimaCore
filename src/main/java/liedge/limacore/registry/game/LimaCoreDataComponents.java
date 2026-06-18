package liedge.limacore.registry.game;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.LimaCore;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
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

    // Energy
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY = nonNegativeInt(LimaCommonConstants.KEY_ENERGY_CONTAINER);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY_CAPACITY = nonNegativeInt(LimaCommonConstants.KEY_ENERGY_CAPACITY);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY_TRANSFER_RATE = nonNegativeInt(LimaCommonConstants.KEY_ENERGY_TRANSFER_RATE);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY_USAGE = nonNegativeInt(LimaCommonConstants.KEY_ENERGY_USAGE);

    // Fluids
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> FLUID_CONTENT = COMPONENTS.registerComponentType(LimaCommonConstants.KEY_SINGLE_FLUID, builder -> builder.persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FluidStackTemplate>> INFINITE_FLUID = COMPONENTS.registerComponentType(LimaCommonConstants.KEY_INFINITE_FLUID, builder -> builder.persistent(FluidStackTemplate.CODEC).networkSynchronized(FluidStackTemplate.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FLUID_CAPACITY = nonNegativeInt(LimaCommonConstants.KEY_FLUID_CAPACITY);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FLUID_TRANSFER_RATE = nonNegativeInt(LimaCommonConstants.KEY_FLUID_TRANSFER_RATE);

    // Misc
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> OWNER = COMPONENTS.registerComponentType(LimaCommonConstants.KEY_OWNER, builder -> builder.persistent(UUIDUtil.CODEC).cacheEncoding());

    private static DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> nonNegativeInt(String name)
    {
        return COMPONENTS.registerComponentType(name, builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    }
}