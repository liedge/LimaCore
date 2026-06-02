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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    @Unique
    private final Deque<List<Pair<Holder<Attribute>, AttributeModifier>>> limacore$modifierStack = new ArrayDeque<>();

    @Inject(method = "hurtServer", at = @At(value = "HEAD"))
    private void applyTransientModifiers(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
    {
        LivingEntity thisEntity = (LivingEntity) (Object) this;
        List<Pair<Holder<Attribute>, AttributeModifier>> pairs = new ObjectArrayList<>();

        NeoForge.EVENT_BUS.post(new DamageAttributeModifiersEvent(thisEntity, level, source, amount, pairs));
        limacore$modifierStack.push(pairs);

        for (var pair : pairs)
        {
            AttributeInstance instance = thisEntity.getAttribute(pair.getA());
            if (instance != null)
            {
                AttributeModifier modifier = pair.getB();
                instance.removeModifier(modifier);
                instance.addOrUpdateTransientModifier(modifier);
            }
        }
    }

    @Inject(method = "hurtServer", at = @At(value = "RETURN"))
    private void clearTransientModifiers(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
    {
        LivingEntity thisEntity = (LivingEntity) (Object) this;

        List<Pair<Holder<Attribute>, AttributeModifier>> pairs = limacore$modifierStack.poll();
        if (pairs != null)
        {
            for (var pair : pairs)
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
}