package liedge.limacore.world.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.LimaCoreLootRegistries;
import liedge.limacore.util.LimaEntityUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;

public record EntityEnchantmentLevelsCondition(LootContext.EntityTarget entityTarget, Holder<Enchantment> enchantment, IntRange validLevels) implements LootItemCondition
{
    public static final MapCodec<EntityEnchantmentLevelsCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LootContext.EntityTarget.CODEC.optionalFieldOf("entity_target", LootContext.EntityTarget.ATTACKER).forGetter(EntityEnchantmentLevelsCondition::entityTarget),
            Enchantment.CODEC.fieldOf("enchantment").forGetter(EntityEnchantmentLevelsCondition::enchantment),
            IntRange.CODEC.fieldOf("valid_levels").forGetter(EntityEnchantmentLevelsCondition::validLevels))
            .apply(instance, EntityEnchantmentLevelsCondition::new));

    public static LootItemCondition.Builder playerHasEnchantment(Holder<Enchantment> holder)
    {
        return playerRequiresAtLeast(holder, 1);
    }

    public static LootItemCondition.Builder playerRequiresAtLeast(Holder<Enchantment> holder, int minimumLevel)
    {
        return () -> new EntityEnchantmentLevelsCondition(LootContext.EntityTarget.ATTACKER, holder, IntRange.lowerBound(minimumLevel));
    }

    @Override
    public LootItemConditionType getType()
    {
        return LimaCoreLootRegistries.ENTITY_ENCHANTMENT_LEVELS_CONDITION.get();
    }

    @Override
    public boolean test(LootContext context)
    {
        int enchantmentLevel = LimaEntityUtil.getEnchantmentLevel(context.getParamOrNull(entityTarget.getParam()), enchantment);
        return validLevels.test(context, enchantmentLevel);
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return Set.of(entityTarget.getParam());
    }
}