package liedge.limacore.advancement;

import liedge.limacore.registry.LimaCoreTriggerTypes;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;

import java.util.Optional;

public final class PlayerLoggedInTrigger extends PlayerTrigger
{
    private PlayerLoggedInTrigger() {}

    public static Criterion<TriggerInstance> playerLoggedIn()
    {
        return LimaCoreTriggerTypes.PLAYER_LOGGED_IN.get().createCriterion(new TriggerInstance(Optional.empty()));
    }

    public static Criterion<TriggerInstance> playerLoggedIn(EntityPredicate.Builder builder)
    {
        return LimaCoreTriggerTypes.PLAYER_LOGGED_IN.get().createCriterion(new TriggerInstance(Optional.of(EntityPredicate.wrap(builder))));
    }
}