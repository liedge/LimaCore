package liedge.limacore.registry;

import liedge.limacore.LimaCore;
import liedge.limacore.recipe.ingredient.ConsumeChanceIngredient;
import liedge.limacore.recipe.ingredient.ItemWithCountCustomIngredient;
import liedge.limacore.recipe.ingredient.TagWithCountCustomIngredient;
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

    public static final DeferredHolder<IngredientType<?>, IngredientType<ItemWithCountCustomIngredient>> ITEM_WITH_COUNT = INGREDIENT_TYPES.register("item_with_count", () -> new IngredientType<>(ItemWithCountCustomIngredient.CODEC));
    public static final DeferredHolder<IngredientType<?>, IngredientType<TagWithCountCustomIngredient>> TAG_WITH_COUNT = INGREDIENT_TYPES.register("tag_with_count", () -> new IngredientType<>(TagWithCountCustomIngredient.CODEC));
    public static final DeferredHolder<IngredientType<?>, IngredientType<ConsumeChanceIngredient>> CONSUME_CHANCE = INGREDIENT_TYPES.register("consume_chance", () -> new IngredientType<>(ConsumeChanceIngredient.CODEC));
}