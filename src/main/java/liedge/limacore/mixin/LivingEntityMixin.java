package liedge.limacore.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import liedge.limacore.LimaCoreTags;
import liedge.limacore.lib.LimaDynamicDamageSource;
import liedge.limacore.util.LimaEntityUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    @WrapOperation(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z", ordinal = 7))
    private boolean checkNoKnockback(DamageSource instance, TagKey<DamageType> damageTypeKey, Operation<Boolean> original)
    {
        if (instance instanceof LimaDynamicDamageSource source)
        {
            return source.getKnockbackMultiplier() == 0f;
        }
        else
        {
            return original.call(instance, damageTypeKey);
        }
    }

    @WrapWithCondition(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    private boolean doCustomKnockback(LivingEntity instance, double strength, double ratioX, double ratioZ, @Local(ordinal = 0, argsOnly = true) DamageSource source)
    {
        boolean bypassResistance = source.is(LimaCoreTags.DamageTypes.IGNORES_KNOCKBACK_RESISTANCE);

        if (source instanceof LimaDynamicDamageSource dynamicDamageSource && dynamicDamageSource.getKnockbackMultiplier() != 1d)
        {
            LimaEntityUtil.customKnockbackEntity(instance, bypassResistance, strength * dynamicDamageSource.getKnockbackMultiplier(), ratioX, ratioZ);
            return false;
        }
        else if (bypassResistance)
        {
            LimaEntityUtil.customKnockbackEntity(instance, true, strength, ratioX, ratioZ);
            return false;
        }

        return true;
    }

    @ModifyArgs(method = "getDamageAfterArmorAbsorb", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/CombatRules;getDamageAfterAbsorb(Lnet/minecraft/world/entity/LivingEntity;FLnet/minecraft/world/damagesource/DamageSource;FF)F"))
    private void modifyArmorValuesFromDamageSource(Args args, @Local(argsOnly = true) DamageSource source)
    {
        if (source instanceof LimaDynamicDamageSource dynamicDamageSource)
        {
            LivingEntity target = args.get(0);
            float armor = args.get(3);
            float armorToughness = args.get(4);

            float newArmor = dynamicDamageSource.modifyAppliedArmor(target, armor);
            float newArmorToughness = dynamicDamageSource.modifyAppliedArmorToughness(target, armorToughness);

            args.set(3, newArmor);
            args.set(4, newArmorToughness);
        }
    }
}