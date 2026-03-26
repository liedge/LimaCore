package liedge.limacore.registry.game;

import com.mojang.serialization.MapCodec;
import liedge.limacore.LimaCore;
import liedge.limacore.world.loot.number.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaCoreNumberProviders
{
    private LimaCoreNumberProviders() {}

    private static final DeferredRegister<MapCodec<? extends NumberProvider>> CODECS = LimaCore.RESOURCES.deferredRegister(Registries.LOOT_NUMBER_PROVIDER_TYPE);

    public static void register(IEventBus bus)
    {
        CODECS.register(bus);
    }

    public static final DeferredHolder<MapCodec<? extends NumberProvider>, MapCodec<LootContextDistance>> LOOT_CONTEXT_DISTANCE = CODECS.register("distance", () -> LootContextDistance.CODEC);
    public static final DeferredHolder<MapCodec<? extends NumberProvider>, MapCodec<RoundValue>> ROUNDING_VALUE = CODECS.register("rounding", () -> RoundValue.CODEC);
    public static final DeferredHolder<MapCodec<? extends NumberProvider>, MapCodec<EntityAttributeValue>> ENTITY_ATTRIBUTE_VALUE = CODECS.register("entity_attribute", () -> EntityAttributeValue.CODEC);
    public static final DeferredHolder<MapCodec<? extends NumberProvider>, MapCodec<EntityEnchantmentLevels>> ENTITY_ENCHANTMENT_LEVEL = CODECS.register("enchantment_level", () -> EntityEnchantmentLevels.CODEC);
    public static final DeferredHolder<MapCodec<? extends NumberProvider>, MapCodec<ValueMathOperation>> MATH_OPERATION = CODECS.register("math", () -> ValueMathOperation.CODEC);
}