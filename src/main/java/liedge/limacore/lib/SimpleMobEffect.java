package liedge.limacore.lib;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Identical to vanilla class, only meant to expose constructors
 */
public class SimpleMobEffect extends MobEffect
{
    public SimpleMobEffect(MobEffectCategory category, int color)
    {
        super(category, color);
    }

    public SimpleMobEffect(MobEffectCategory category, int color, ParticleOptions particle)
    {
        super(category, color, particle);
    }
}