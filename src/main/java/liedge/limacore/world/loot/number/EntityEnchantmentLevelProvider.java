package liedge.limacore.world.loot.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import liedge.limacore.util.LimaEntityUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.Set;

public record EntityEnchantmentLevelProvider(LootContext.EntityTarget target, Holder<Enchantment> enchantment) implements NumberProvider
{
    public static final MapCodec<EntityEnchantmentLevelProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LootContext.EntityTarget.CODEC.fieldOf("target").forGetter(EntityEnchantmentLevelProvider::target),
            Enchantment.CODEC.fieldOf("enchantment").forGetter(EntityEnchantmentLevelProvider::enchantment))
            .apply(instance, EntityEnchantmentLevelProvider::new));

    public static EntityEnchantmentLevelProvider enchantLevel(LootContext.EntityTarget target, Holder<Enchantment> enchantment)
    {
        return new EntityEnchantmentLevelProvider(target, enchantment);
    }

    @Override
    public float getFloat(LootContext context)
    {
        return LimaEntityUtil.getEnchantmentLevel(context.getParamOrNull(target.getParam()), enchantment);
    }

    @Override
    public LootNumberProviderType getType()
    {
        return LimaCoreLootRegistries.ENTITY_ENCHANTMENT_LEVEL_PROVIDER.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return Set.of(target.getParam());
    }
}