package liedge.limacore.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import liedge.limacore.lib.LimaDynamicDamageSource;
import liedge.limacore.util.LimaCoreUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LootItemRandomChanceWithEnchantedBonusCondition.class)
public abstract class LootItemRandomChanceMixin
{
    @WrapOperation(method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/LivingEntity;)I"))
    private int applyDamageSourceEnchantmentLevels(Holder<Enchantment> enchantment, LivingEntity entity, Operation<Integer> original, @Local(argsOnly = true) LootContext context)
    {
        final int entityLevel = original.call(enchantment, entity);
        LimaDynamicDamageSource dynamicSource = LimaCoreUtil.castOrNull(LimaDynamicDamageSource.class, context.getParamOrNull(LootContextParams.DAMAGE_SOURCE));
        return dynamicSource != null ? dynamicSource.modifyEnchantmentLevel(context, enchantment, entityLevel) : entityLevel;
    }
}