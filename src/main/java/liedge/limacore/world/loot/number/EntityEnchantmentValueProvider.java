package liedge.limacore.world.loot.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import liedge.limacore.util.LimaEntityUtil;
import net.minecraft.core.Holder;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.Optional;
import java.util.Set;

public record EntityEnchantmentValueProvider(LootContext.EntityTarget target, Holder<Enchantment> enchantment, NumberProvider unenchanted, Optional<LevelBasedValue> amount) implements NumberProvider
{
    public static final MapCodec<EntityEnchantmentValueProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LootContext.EntityTarget.CODEC.fieldOf("target").forGetter(EntityEnchantmentValueProvider::target),
            Enchantment.CODEC.fieldOf("enchantment").forGetter(EntityEnchantmentValueProvider::enchantment),
            NumberProviders.CODEC.fieldOf("unenchanted").forGetter(EntityEnchantmentValueProvider::unenchanted),
            LevelBasedValue.CODEC.optionalFieldOf("amount").forGetter(EntityEnchantmentValueProvider::amount))
            .apply(instance, EntityEnchantmentValueProvider::new));

    public static EntityEnchantmentValueProvider enchantedValue(LootContext.EntityTarget target, Holder<Enchantment> enchantment, NumberProvider unenchanted, LevelBasedValue amount)
    {
        return new EntityEnchantmentValueProvider(target, enchantment, unenchanted, Optional.of(amount));
    }

    public static EntityEnchantmentValueProvider levelOnly(LootContext.EntityTarget target, Holder<Enchantment> enchantment)
    {
        return new EntityEnchantmentValueProvider(target, enchantment, ConstantValue.exactly(0), Optional.empty());
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
    public LootNumberProviderType getType()
    {
        return LimaCoreLootRegistries.ENTITY_ENCHANTMENT_VALUE_PROVIDER.get();
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