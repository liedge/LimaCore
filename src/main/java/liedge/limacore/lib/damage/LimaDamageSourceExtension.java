package liedge.limacore.lib.damage;

import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.MutableDataComponentHolder;

import java.util.Collection;
import java.util.Set;

/**
 * Extension of {@link MutableDataComponentHolder} with all methods made default
 * to meet the interface injection mixin requirements.
 * @apiNote For use by {@link net.minecraft.world.damagesource.DamageSource} mixin only. Do not implement.
 */
public interface LimaDamageSourceExtension
{
    default Set<TagKey<DamageType>> limaCore$getExtraTags()
    {
        throw new UnsupportedOperationException("Interface LimaDamageSourceExtension implementation must override getExtraTags");
    }

    default void limaCore$addExtraTags(Collection<TagKey<DamageType>> tags)
    {
        limaCore$getExtraTags().addAll(tags);
    }

    default void limaCore$addExtraTag(TagKey<DamageType> tag)
    {
        limaCore$getExtraTags().add(tag);
    }
}