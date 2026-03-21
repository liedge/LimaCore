package liedge.limacore.registry.game;

import liedge.limacore.LimaCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaCoreRecipes
{
    private LimaCoreRecipes() {}

    private static final DeferredRegister<RecipeBookCategory> BOOK_CATEGORIES = LimaCore.RESOURCES.deferredRegister(Registries.RECIPE_BOOK_CATEGORY);

    public static void register(IEventBus modBus)
    {
        modBus.register(BOOK_CATEGORIES);
    }

    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> CUSTOM_RECIPE_CATEGORY = BOOK_CATEGORIES.register("custom", RecipeBookCategory::new);
}