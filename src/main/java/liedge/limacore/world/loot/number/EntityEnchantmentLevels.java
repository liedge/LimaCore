package liedge.limacore.world.loot.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import liedge.limacore.util.LimaEntityUtil;
import net.minecraft.core.Holder;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.Optional;
import java.util.Set;

public record EntityEnchantmentLevels(LootContext.EntityTarget target, Holder<Enchantment> enchantment, NumberProvider unenchanted, Optional<LevelBasedValue> amount) implements NumberProvider
{
    public static final MapCodec<EntityEnchantmentLevels> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LootContext.EntityTarget.CODEC.fieldOf("target").forGetter(EntityEnchantmentLevels::target),
            Enchantment.CODEC.fieldOf("enchantment").forGetter(EntityEnchantmentLevels::enchantment),
            NumberProviders.CODEC.fieldOf("unenchanted").forGetter(EntityEnchantmentLevels::unenchanted),
            LevelBasedValue.CODEC.optionalFieldOf("amount").forGetter(EntityEnchantmentLevels::amount))
            .apply(instance, EntityEnchantmentLevels::new));

    public static EntityEnchantmentLevels enchantedValue(LootContext.EntityTarget target, Holder<Enchantment> enchantment, NumberProvider unenchanted, LevelBasedValue amount)
    {
        return new EntityEnchantmentLevels(target, enchantment, unenchanted, Optional.of(amount));
    }

    public static EntityEnchantmentLevels levelOnly(LootContext.EntityTarget target, Holder<Enchantment> enchantment)
    {
        return new EntityEnchantmentLevels(target, enchantment, ConstantValue.exactly(0), Optional.empty());
    }

    @Override
    public float getFloat(LootContext context)
    {
        int level = LimaEntityUtil.getEnchantmentLevel(context.getOptionalParameter(target.contextParam()), enchantment);
        if (level < 1)
            return unenchanted.getFloat(context);
        else
            return amount.isPresent() ? amount.get().calculate(level) : level;
    }

    @Override
    public MapCodec<? extends NumberProvider> codec()
    {
        return CODEC;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams()
    {
        ObjectSet<ContextKey<?>> usedKeys = new ObjectOpenHashSet<>();
        usedKeys.addAll(unenchanted.getReferencedContextParams());
        usedKeys.add(target.contextParam());

        return ObjectSets.unmodifiable(usedKeys);
    }
}