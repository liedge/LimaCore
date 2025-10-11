package liedge.limacore.world.loot.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import liedge.limacore.util.LimaEntityUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.Set;

public record EntityAttributeValueProvider(LootContext.EntityTarget target, Holder<Attribute> attribute, boolean base) implements NumberProvider
{
    public static final MapCodec<EntityAttributeValueProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LootContext.EntityTarget.CODEC.fieldOf("target").forGetter(EntityAttributeValueProvider::target),
            Attribute.CODEC.fieldOf("attribute").forGetter(EntityAttributeValueProvider::attribute),
            Codec.BOOL.optionalFieldOf("base", false).forGetter(EntityAttributeValueProvider::base))
            .apply(instance, EntityAttributeValueProvider::new));

    public static NumberProvider totalValue(LootContext.EntityTarget target, Holder<Attribute> attribute)
    {
        return new EntityAttributeValueProvider(target, attribute, false);
    }

    public static NumberProvider baseValue(LootContext.EntityTarget target, Holder<Attribute> attribute)
    {
        return new EntityAttributeValueProvider(target, attribute, true);
    }

    @Override
    public float getFloat(LootContext ctx)
    {
        Entity entity = ctx.getParamOrNull(target.getParam());
        double value = base ? LimaEntityUtil.getAttributeBaseValueSafe(entity, attribute) : LimaEntityUtil.getAttributeValueSafe(entity, attribute);
        return (float) value;
    }

    @Override
    public LootNumberProviderType getType()
    {
        return LimaCoreLootRegistries.ENTITY_ATTRIBUTE_VALUE_PROVIDER.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return Set.of(target.getParam());
    }
}