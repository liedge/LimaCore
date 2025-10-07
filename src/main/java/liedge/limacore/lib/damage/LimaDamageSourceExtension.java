package liedge.limacore.lib.damage;

import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.MutableDataComponentHolder;

/**
 * Extension of {@link MutableDataComponentHolder} with all methods made default
 * to meet the interface injection mixin requirements.
 * @apiNote For use by {@link net.minecraft.world.damagesource.DamageSource} mixin only. Do not implement.
 */
public interface LimaDamageSourceExtension
{
    default void limaCore$addDynamicTag(TagKey<DamageType> tag)
    {
        throw new IllegalArgumentException("Interface LimaDamageSourceExtension implementation must override addDynamicTag");
    }
}