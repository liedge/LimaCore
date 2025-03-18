package liedge.limacore.lib;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class LimaDynamicDamageSource extends DamageSource
{
    private final Set<TagKey<DamageType>> dynamicTagKeys;

    private double knockbackMultiplier = 1d;
    private float armorModifier;
    private float armorToughnessModifier;

    public LimaDynamicDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 damageSourcePosition)
    {
        super(type, directEntity, causingEntity, damageSourcePosition);
        this.dynamicTagKeys = new ObjectOpenHashSet<>();
    }

    public LimaDynamicDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity)
    {
        this(type, directEntity, causingEntity, null);
    }

    public LimaDynamicDamageSource(Holder<DamageType> type, Vec3 damageSourcePosition)
    {
        this(type, null, null, damageSourcePosition);
    }

    public LimaDynamicDamageSource(Holder<DamageType> type, @Nullable Entity entity)
    {
        this(type, entity, entity);
    }

    public LimaDynamicDamageSource(Holder<DamageType> type)
    {
        this(type, null, null, null);
    }

    @Override
    public boolean is(TagKey<DamageType> damageTypeKey)
    {
        return dynamicTagKeys.contains(damageTypeKey) || super.is(damageTypeKey);
    }

    public void addDynamicTag(TagKey<DamageType> damageTypeTag)
    {
        dynamicTagKeys.add(damageTypeTag);
    }

    public void removeDynamicTag(TagKey<DamageType> damageTypeTag)
    {
        dynamicTagKeys.remove(damageTypeTag);
    }

    public double getKnockbackMultiplier()
    {
        return knockbackMultiplier;
    }

    public void setKnockbackMultiplier(double knockbackMultiplier)
    {
        this.knockbackMultiplier = knockbackMultiplier;
    }

    public float getArmorModifier()
    {
        return armorModifier;
    }

    public float getArmorToughnessModifier()
    {
        return armorToughnessModifier;
    }

    public void setArmorModifier(float armorModifier)
    {
        this.armorModifier = armorModifier;
    }

    public void setArmorToughnessModifier(float armorToughnessModifier)
    {
        this.armorToughnessModifier = armorToughnessModifier;
    }

    /**
     * Modifies the armor value before damage from this source is calculated
     * @param target The living entity wearing the armor
     * @param armor The current armor value
     * @return The new armor value to be used in calculating damage reduction
     */
    public float modifyAppliedArmor(LivingEntity target, float armor)
    {
        return Math.max(0, armor + armorModifier);
    }

    /**
     * Modifies the armor toughness value before damage from this source is calculated
     * @param target The living entity wearing the armor
     * @param armorToughness The current armor toughness
     * @return The new armor toughness value to be used in calculating damage reduction
     */
    public float modifyAppliedArmorToughness(LivingEntity target, float armorToughness)
    {
        return Math.max(0, armorToughness + armorToughnessModifier);
    }

    public int modifyEnchantmentLevel(LootContext context, Holder<Enchantment> enchantment, int entityLevel)
    {
        return entityLevel;
    }
}