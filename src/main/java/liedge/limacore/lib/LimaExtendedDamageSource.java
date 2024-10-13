package liedge.limacore.lib;

import liedge.limacore.LimaCoreTags;
import net.minecraft.core.Holder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class LimaExtendedDamageSource extends DamageSource
{
    private boolean noAnger = false;
    private boolean bypassArmor = false;
    private boolean bypassKnockbackResistance = false;
    private double knockbackMultiplier = 1d;

    public LimaExtendedDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 damageSourcePosition)
    {
        super(type, directEntity, causingEntity, damageSourcePosition);
    }

    public LimaExtendedDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity)
    {
        super(type, directEntity, causingEntity);
    }

    public LimaExtendedDamageSource(Holder<DamageType> type, Vec3 damageSourcePosition)
    {
        super(type, damageSourcePosition);
    }

    public LimaExtendedDamageSource(Holder<DamageType> type, @Nullable Entity entity)
    {
        super(type, entity);
    }

    public LimaExtendedDamageSource(Holder<DamageType> type)
    {
        super(type);
    }

    public boolean isNoAnger()
    {
        return noAnger || is(DamageTypeTags.NO_ANGER);
    }

    public void setNoAnger(boolean noAnger)
    {
        this.noAnger = noAnger;
    }

    public boolean isBypassArmor()
    {
        return bypassArmor || is(DamageTypeTags.BYPASSES_ARMOR);
    }

    public void setBypassArmor(boolean bypassArmor)
    {
        this.bypassArmor = bypassArmor;
    }

    public boolean isBypassKnockbackResistance()
    {
        return bypassKnockbackResistance || is(LimaCoreTags.DamageTypes.IGNORES_KNOCKBACK_RESISTANCE);
    }

    public void setBypassKnockbackResistance(boolean bypassKnockbackResistance)
    {
        this.bypassKnockbackResistance = bypassKnockbackResistance;
    }

    public double getKnockbackMultiplier()
    {
        return knockbackMultiplier;
    }

    public void setKnockbackMultiplier(double knockbackMultiplier)
    {
        this.knockbackMultiplier = knockbackMultiplier;
    }

    /**
     * Modifies the armor value before damage from this source is calculated
     * @param target The living entity wearing the armor
     * @param armor The current armor value
     * @return The new armor value to be used in calculating damage reduction
     */
    public float modifyAppliedArmor(LivingEntity target, float armor)
    {
        return armor;
    }

    /**
     * Modifies the armor toughness value before damage from this source is calculated
     * @param target The living entity wearing the armor
     * @param armorToughness The current armor toughness
     * @return The new armor toughness value to be used in calculating damage reduction
     */
    public float modifyAppliedArmorToughness(LivingEntity target, float armorToughness)
    {
        return armorToughness;
    }
}