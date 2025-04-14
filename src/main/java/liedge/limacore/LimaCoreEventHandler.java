package liedge.limacore;

import liedge.limacore.lib.damage.LimaCoreDamageComponents;
import liedge.limacore.lib.damage.ReductionModifier;
import liedge.limacore.registry.game.LimaCoreAttributes;
import liedge.limacore.registry.game.LimaCoreTriggerTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.List;

@EventBusSubscriber(modid = LimaCore.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class LimaCoreEventHandler
{
    private LimaCoreEventHandler() {}

    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer serverPlayer)
        {
            LimaCoreTriggerTypes.PLAYER_LOGGED_IN.get().trigger(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onLivingKnockback(final LivingKnockBackEvent event)
    {
        double modifier = event.getEntity().getAttributeValue(LimaCoreAttributes.KNOCKBACK_MULTIPLIER);
        if (modifier == 1) return;

        double newStrength = event.getStrength() * modifier;
        event.setStrength((float) newStrength);
    }

    // Run this on high. There might be other sources that modify the damage (highest priority) but we should
    // still apply as early as possible.
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingIncomingDamage(final LivingIncomingDamageEvent event)
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

        // Apply damage reduction modifiers
        List<ReductionModifier> reductionModifiers = damageSource.getList(LimaCoreDamageComponents.REDUCTION_MODIFIERS);
        for (ReductionModifier modifier : reductionModifiers)
        {
            event.addReductionModifier(modifier.reductionType().getReduction(), (container, reduction) -> (float) modifier.operation().applyAsDouble(reduction, modifier.amount()));
        }

        // Apply damage-context transient modifiers
        List<ItemAttributeModifiers.Entry> modifierEntries = damageSource.getList(LimaCoreDamageComponents.ATTRIBUTE_MODIFIERS);
        AttributeMap attributes = event.getEntity().getAttributes();
        for (ItemAttributeModifiers.Entry entry : modifierEntries)
        {
            AttributeInstance instance = attributes.getInstance(entry.attribute());
            if (instance != null)
            {
                instance.removeModifier(entry.modifier());
                instance.addTransientModifier(entry.modifier());
            }
        }
    }
}