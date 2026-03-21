package liedge.limacore.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.event.DamageAttributeModifiersEvent;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import oshi.util.tuples.Pair;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    @Unique
    private final List<Pair<Holder<Attribute>, AttributeModifier>> limacore$modifiers = new ObjectArrayList<>();

    @Inject(method = "hurtServer", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/neoforged/neoforge/common/damagesource/DamageContainer;getNewDamage()F", ordinal = 0))
    private void serverPreDamage(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
    {
        // Modifiers
        limacore$modifiers.clear();
        NeoForge.EVENT_BUS.post(new DamageAttributeModifiersEvent(level, source, amount, limacore$modifiers));
        LivingEntity thisEntity = (LivingEntity) (Object) this;

        for (var pair : limacore$modifiers)
        {
            AttributeInstance instance = thisEntity.getAttribute(pair.getA());
            if (instance != null)
            {
                AttributeModifier modifier = pair.getB();
                instance.removeModifier(modifier);
                instance.addTransientModifier(modifier);
            }
        }
    }

    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Ljava/util/Stack;pop()Ljava/lang/Object;", ordinal = 1))
    private void serverPostDamage(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
    {
        LivingEntity thisEntity = (LivingEntity) (Object) this;

        for (var pair : limacore$modifiers)
        {
            AttributeInstance instance = thisEntity.getAttribute(pair.getA());
            if (instance != null)
            {
                AttributeModifier modifier = pair.getB();
                instance.removeModifier(modifier);
            }
        }
    }
}