package liedge.limacore.registry.game;

import liedge.limacore.LimaCore;
import liedge.limacore.recipe.ingredient.ConsumeChanceIngredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class LimaCoreIngredientTypes
{
    private LimaCoreIngredientTypes() {}

    private static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = LimaCore.RESOURCES.deferredRegister(NeoForgeRegistries.INGREDIENT_TYPES);

    public static void register(IEventBus bus)
    {
        INGREDIENT_TYPES.register(bus);
    }

    public static final DeferredHolder<IngredientType<?>, IngredientType<ConsumeChanceIngredient>> CONSUME_CHANCE = INGREDIENT_TYPES.register("consume_chance", () -> new IngredientType<>(ConsumeChanceIngredient.CODEC));
}