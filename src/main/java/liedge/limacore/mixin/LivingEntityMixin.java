package liedge.limacore.mixin;

import liedge.limacore.lib.damage.LimaCoreDamageComponents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Ljava/util/Stack;pop()Ljava/lang/Object;", ordinal = 1))
    private void trueOnPostDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
    {
        LivingEntity thisEntity = (LivingEntity) (Object) this;
        List<ItemAttributeModifiers.Entry> modifierEntries = source.getList(LimaCoreDamageComponents.ATTRIBUTE_MODIFIERS);
        AttributeMap attributes = thisEntity.getAttributes();

        for (ItemAttributeModifiers.Entry entry : modifierEntries)
        {
            AttributeInstance instance = attributes.getInstance(entry.attribute());
            if (instance != null) instance.removeModifier(entry.modifier());
        }
    }
}