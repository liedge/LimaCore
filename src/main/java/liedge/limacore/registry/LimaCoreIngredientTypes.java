package liedge.limacore.registry;

import liedge.limacore.LimaCore;
import liedge.limacore.recipe.LimaSimpleCountIngredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class LimaCoreIngredientTypes
{
    private LimaCoreIngredientTypes() {}

    private static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = LimaCore.RESOURCES.deferredRegister(NeoForgeRegistries.INGREDIENT_TYPES);

    public static void init(IEventBus bus)
    {
        INGREDIENT_TYPES.register(bus);
    }

    public static DeferredHolder<IngredientType<?>, IngredientType<LimaSimpleCountIngredient>> LIMA_SIMPLE_COUNT_INGREDIENT = INGREDIENT_TYPES.register("simple_count", () -> new IngredientType<>(LimaSimpleCountIngredient.INGREDIENT_MAP_CODEC));
}