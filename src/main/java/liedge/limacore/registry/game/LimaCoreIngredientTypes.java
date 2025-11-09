package liedge.limacore.registry.game;

import liedge.limacore.LimaCore;
import liedge.limacore.recipe.ingredient.DeterministicFluidIngredient;
import liedge.limacore.recipe.ingredient.DeterministicItemIngredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class LimaCoreIngredientTypes
{
    private LimaCoreIngredientTypes() {}

    private static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = LimaCore.RESOURCES.deferredRegister(NeoForgeRegistries.INGREDIENT_TYPES);
    private static final DeferredRegister<FluidIngredientType<?>> FLUID_INGREDIENT_TYPES = LimaCore.RESOURCES.deferredRegister(NeoForgeRegistries.FLUID_INGREDIENT_TYPES);

    public static void register(IEventBus bus)
    {
        INGREDIENT_TYPES.register(bus);
        FLUID_INGREDIENT_TYPES.register(bus);
    }

    public static final DeferredHolder<IngredientType<?>, IngredientType<DeterministicItemIngredient>> DETERMINISTIC_ITEM = INGREDIENT_TYPES.register("deterministic", () -> new IngredientType<>(DeterministicItemIngredient.CODEC));
    public static final DeferredHolder<FluidIngredientType<?>, FluidIngredientType<DeterministicFluidIngredient>> DETERMINISTIC_FLUID = FLUID_INGREDIENT_TYPES.register("deterministic", () -> new FluidIngredientType<>(DeterministicFluidIngredient.CODEC));
}