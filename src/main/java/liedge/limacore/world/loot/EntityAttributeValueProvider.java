package liedge.limacore.world.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.LimaCoreLootRegistries;
import liedge.limacore.util.LimaEntityUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record EntityAttributeValueProvider(LootContext.EntityTarget entityTarget, Holder<Attribute> attribute, NumberProvider factor, NumberProvider fallback) implements NumberProvider
{
    public static final MapCodec<EntityAttributeValueProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LootContext.EntityTarget.CODEC.fieldOf("entity_target").forGetter(EntityAttributeValueProvider::entityTarget),
            Attribute.CODEC.fieldOf("attribute").forGetter(EntityAttributeValueProvider::attribute),
            NumberProviders.CODEC.fieldOf("factor").forGetter(EntityAttributeValueProvider::factor),
            NumberProviders.CODEC.fieldOf("fallback").forGetter(EntityAttributeValueProvider::fallback))
            .apply(instance, EntityAttributeValueProvider::new));

    @Override
    public float getFloat(LootContext ctx)
    {
        AttributeInstance instance = LimaEntityUtil.getAttributeInstanceSafe(ctx.getParamOrNull(entityTarget.getParam()), attribute);
        return instance != null ? (float) instance.getValue() * factor.getFloat(ctx) : fallback.getFloat(ctx);
    }

    @Override
    public LootNumberProviderType getType()
    {
        return LimaCoreLootRegistries.ENTITY_ATTRIBUTE_VALUE_NUMBER_PROVIDER.get();
    }
}