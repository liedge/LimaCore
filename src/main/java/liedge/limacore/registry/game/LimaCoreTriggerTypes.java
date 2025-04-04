package liedge.limacore.registry.game;

import liedge.limacore.LimaCore;
import liedge.limacore.advancement.CustomRecipeTypeTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaCoreTriggerTypes
{
    private LimaCoreTriggerTypes () {}

    private static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPES = LimaCore.RESOURCES.deferredRegister(BuiltInRegistries.TRIGGER_TYPES);

    public static void register(IEventBus bus)
    {
        TRIGGER_TYPES.register(bus);
    }

    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> PLAYER_LOGGED_IN = TRIGGER_TYPES.register("player_logged_in", PlayerTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, CustomRecipeTypeTrigger> CUSTOM_RECIPE_TYPE_USED = TRIGGER_TYPES.register("custom_recipe_type_used", CustomRecipeTypeTrigger::new);
}