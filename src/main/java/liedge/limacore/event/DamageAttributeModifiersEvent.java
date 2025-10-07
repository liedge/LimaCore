package liedge.limacore.event;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.bus.api.Event;
import oshi.util.tuples.Pair;

import java.util.List;

/**
 * Allows application of transient {@link AttributeModifier} instances to a
 * living entity when it is being attacked. These modifiers are removed at the end of
 * {@link net.minecraft.world.entity.LivingEntity#hurt(DamageSource, float)}.
 */
public final class DamageAttributeModifiersEvent extends Event
{
    private final DamageSource damageSource;
    private final float damage;
    private final List<Pair<Holder<Attribute>, AttributeModifier>> modifiers;

    public DamageAttributeModifiersEvent(DamageSource damageSource, float damage, List<Pair<Holder<Attribute>, AttributeModifier>> modifiers)
    {
        this.damageSource = damageSource;
        this.damage = damage;
        this.modifiers = modifiers;
    }

    public DamageSource getDamageSource()
    {
        return damageSource;
    }

    public float getDamage()
    {
        return damage;
    }

    public void addAttributeModifier(Holder<Attribute> attribute, AttributeModifier modifier)
    {
        modifiers.add(new Pair<>(attribute, modifier));
    }
}