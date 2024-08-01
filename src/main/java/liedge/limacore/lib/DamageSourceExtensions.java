package liedge.limacore.lib;

public interface DamageSourceExtensions
{
    /**
     * Functionally equivalent to the Vanilla 'no_anger' tag, but able to be determined dynamically.
     * @return True if the damage source will avoid anger.
     */
    default boolean avoidsAngering()
    {
        return false;
    }

    /**
     * Allows the damage source to ignore a percentage of or all armor.
     * Values greater than or equal to 1 bypass armor completely and does not damage armor durability.
     * @return From 0 to 1, the percentage of armor bypassed.
     */
    default float getArmorBypassAmount()
    {
        return 0f;
    }

    /**
     * Allows the damage source to ignore a percentage of or all armor toughness.
     * Values greater than or equal to 1 bypass all armor toughness.
     * @return From 0 to 1, the percentage of armor toughness bypassed.
     */
    default float getArmorToughnessBypassAmount()
    {
        return 0f;
    }

    /**
     * Allows the damage source to knock back the entity a different amount than normal.
     * A value of 0 is equivalent to the 'no knockback' damage type tag. This value can be modified by
     * {@link net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent}
     * or by the target's Knockback Resistance attribute.
     * @return The amount the knockback will be multiplied by.
     */
    default float getKnockbackModifier()
    {
        return 1f;
    }

    /**
     * Allows the damage source to bypass the Knockback Resistance attribute only. This does not affect
     * {@link net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent}, cancelling that event
     * supersedes this method.
     * @return Whether the knockback caused by this damage source will ignore the Knockback Resistance attribute.
     */
    default boolean ignoreKnockbackResistance()
    {
        return false;
    }
}