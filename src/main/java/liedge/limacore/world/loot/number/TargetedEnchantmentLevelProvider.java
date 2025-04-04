package liedge.limacore.world.loot.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.LimaCoreLootRegistries;
import liedge.limacore.util.LimaEntityUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.*;

import java.util.Optional;

public record TargetedEnchantmentLevelProvider(LootContext.EntityTarget target, Holder<Enchantment> enchantment, LevelBasedValue amount, Optional<LevelBasedValue> fallback) implements NumberProvider
{
    public static final MapCodec<TargetedEnchantmentLevelProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LootContext.EntityTarget.CODEC.optionalFieldOf("target", LootContext.EntityTarget.ATTACKER).forGetter(TargetedEnchantmentLevelProvider::target),
            Enchantment.CODEC.fieldOf("enchantment").forGetter(TargetedEnchantmentLevelProvider::enchantment),
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(TargetedEnchantmentLevelProvider::amount),
            LevelBasedValue.CODEC.optionalFieldOf("fallback").forGetter(TargetedEnchantmentLevelProvider::fallback))
            .apply(instance, TargetedEnchantmentLevelProvider::new));

    public static NumberProvider of(LootContext.EntityTarget target, Holder<Enchantment> enchantment, LevelBasedValue amount)
    {
        return new TargetedEnchantmentLevelProvider(target, enchantment, amount, Optional.empty());
    }

    public static NumberProvider of(LootContext.EntityTarget target, Holder<Enchantment> enchantment, LevelBasedValue amount, float constantFallback)
    {
        return new TargetedEnchantmentLevelProvider(target, enchantment, amount, Optional.of(LevelBasedValue.constant(constantFallback)));
    }

    @Override
    public float getFloat(LootContext ctx)
    {
        int enchantmentLevel = LimaEntityUtil.getEnchantmentLevel(ctx.getParamOrNull(target.getParam()), enchantment);
        float value = amount.calculate(enchantmentLevel);
        return enchantmentLevel > 0 ? value : fallback.map(o -> o.calculate(0)).orElse(value);
    }

    @Override
    public LootNumberProviderType getType()
    {
        return LimaCoreLootRegistries.TARGETED_ENCHANTMENT_LEVEL_NUMBER_PROVIDER.get();
    }
}