package liedge.limacore.event;

import liedge.limacore.LimaCore;
import liedge.limacore.registry.game.LimaCoreAttributes;
import liedge.limacore.registry.game.LimaCoreTriggerTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = LimaCore.MODID)
public final class LimaCoreEventHandler
{
    private LimaCoreEventHandler() {}

    @SubscribeEvent
    static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer serverPlayer)
        {
            LimaCoreTriggerTypes.PLAYER_LOGGED_IN.get().trigger(serverPlayer);
        }
    }

    @SubscribeEvent
    static void onLivingKnockback(final LivingKnockBackEvent event)
    {
        double modifier = event.getEntity().getAttributeValue(LimaCoreAttributes.KNOCKBACK_MULTIPLIER);
        if (modifier == 1) return;

        double newStrength = event.getStrength() * modifier;
        event.setStrength((float) newStrength);
    }

    // We can run on highest priority now, modifiers are handled at proper time with mixin
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    static void onLivingIncomingDamage(final LivingIncomingDamageEvent event)
    {
        DamageSource damageSource = event.getSource();

        // Apply the damage multiplier attribute to the attacker's damage
        if (damageSource.getEntity() instanceof LivingEntity attacker)
        {
            double damageModifier = attacker.getAttributeValue(LimaCoreAttributes.DAMAGE_MULTIPLIER);
            if (damageModifier != 1)
            {
                double newDamage = event.getAmount() * damageModifier;
                event.setAmount((float) newDamage);
            }
        }
    }
}