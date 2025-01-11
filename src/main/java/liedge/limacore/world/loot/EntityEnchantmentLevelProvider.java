package liedge.limacore.world.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.LimaCoreLootRegistries;
import liedge.limacore.util.LimaEntityUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record EntityEnchantmentLevelProvider(LootContext.EntityTarget entityTarget, Holder<Enchantment> enchantment, LevelBasedValue enchantedValue, NumberProvider normalValue) implements NumberProvider
{
    public static final MapCodec<EntityEnchantmentLevelProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LootContext.EntityTarget.CODEC.optionalFieldOf("entity_target", LootContext.EntityTarget.ATTACKER).forGetter(EntityEnchantmentLevelProvider::entityTarget),
            Enchantment.CODEC.fieldOf("enchantment").forGetter(EntityEnchantmentLevelProvider::enchantment),
            LevelBasedValue.CODEC.fieldOf("enchanted_value").forGetter(EntityEnchantmentLevelProvider::enchantedValue),
            NumberProviders.CODEC.optionalFieldOf("normal_value", ConstantValue.exactly(0)).forGetter(EntityEnchantmentLevelProvider::normalValue))
            .apply(instance, EntityEnchantmentLevelProvider::new));

    public static EntityEnchantmentLevelProvider requirePlayerEnchantLevel(Holder<Enchantment> enchantment, LevelBasedValue enchantedValue)
    {
        return playerEnchantLevelOrElse(enchantment, enchantedValue, 0);
    }

    public static EntityEnchantmentLevelProvider playerEnchantLevelOrElse(Holder<Enchantment> enchantment, LevelBasedValue enchantedValue, NumberProvider normalValue)
    {
        return new EntityEnchantmentLevelProvider(LootContext.EntityTarget.ATTACKER, enchantment, enchantedValue, normalValue);
    }

    public static EntityEnchantmentLevelProvider playerEnchantLevelOrElse(Holder<Enchantment> enchantment, LevelBasedValue enchantedValue, float constantNormalValue)
    {
        return playerEnchantLevelOrElse(enchantment, enchantedValue, ConstantValue.exactly(constantNormalValue));
    }

    @Override
    public float getFloat(LootContext ctx)
    {
        int enchantmentLevel = LimaEntityUtil.getEnchantmentLevel(ctx.getParamOrNull(entityTarget.getParam()), enchantment);
        return enchantmentLevel > 0 ? enchantedValue.calculate(enchantmentLevel) : normalValue.getFloat(ctx);
    }

    @Override
    public LootNumberProviderType getType()
    {
        return LimaCoreLootRegistries.ENTITY_ENCHANTMENT_LEVEL_NUMBER_PROVIDER.get();
    }
}