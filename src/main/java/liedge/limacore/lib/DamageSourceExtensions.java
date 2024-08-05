package liedge.limacore.lib;

import net.minecraft.world.entity.LivingEntity;

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
     * Functionally equivalent to the Vanilla 'bypasses_armor' tag, but able to be determined dynamically.
     * @return True if armor is bypassed completely.
     */
    default boolean bypassesArmor()
    {
        return false;
    }

    /**
     * Modifies the armor value before damage from this source is calculated
     * @param armorWearer The living entity wearing the armor
     * @param armor The current armor value
     * @return The new armor value to be used in calculating damage reduction
     */
    default float modifyAppliedArmor(LivingEntity armorWearer, float armor)
    {
        return armor;
    }

    /**
     * Modifies the armor toughness value before damage from this source is calculated
     * @param armorWearer The living entity wearing the armor
     * @param armorToughness The current armor toughness
     * @return The new armor toughness value to be used in calculating damage reduction
     */
    default float modifyAppliedArmorToughness(LivingEntity armorWearer, float armorToughness)
    {
        return armorToughness;
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