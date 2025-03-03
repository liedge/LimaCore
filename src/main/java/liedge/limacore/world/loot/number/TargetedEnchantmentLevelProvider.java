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

public record TargetedEnchantmentLevelProvider(LootContext.EntityTarget target, Holder<Enchantment> enchantment, LevelBasedValue amount) implements NumberProvider
{
    public static final MapCodec<TargetedEnchantmentLevelProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LootContext.EntityTarget.CODEC.optionalFieldOf("target", LootContext.EntityTarget.ATTACKER).forGetter(TargetedEnchantmentLevelProvider::target),
            Enchantment.CODEC.fieldOf("enchantment").forGetter(TargetedEnchantmentLevelProvider::enchantment),
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(TargetedEnchantmentLevelProvider::amount))
            .apply(instance, TargetedEnchantmentLevelProvider::new));

    public static NumberProvider of(LootContext.EntityTarget target, Holder<Enchantment> enchantment, LevelBasedValue amount)
    {
        return new TargetedEnchantmentLevelProvider(target, enchantment, amount);
    }

    @Override
    public float getFloat(LootContext ctx)
    {
        int enchantmentLevel = LimaEntityUtil.getEnchantmentLevel(ctx.getParamOrNull(target.getParam()), enchantment);
        return enchantmentLevel > 0 ? amount.calculate(enchantmentLevel) : 0f;
    }

    @Override
    public LootNumberProviderType getType()
    {
        return LimaCoreLootRegistries.TARGETED_ENCHANTMENT_LEVEL_NUMBER_PROVIDER.get();
    }
}