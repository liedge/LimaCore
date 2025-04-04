package liedge.limacore.world.loot.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import liedge.limacore.util.LimaEntityUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public record TargetedAttributeValueProvider(LootContext.EntityTarget target, Holder<Attribute> attribute) implements NumberProvider
{
    public static final MapCodec<TargetedAttributeValueProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LootContext.EntityTarget.CODEC.fieldOf("target").forGetter(TargetedAttributeValueProvider::target),
            Attribute.CODEC.fieldOf("attribute").forGetter(TargetedAttributeValueProvider::attribute))
            .apply(instance, TargetedAttributeValueProvider::new));

    public static NumberProvider of(LootContext.EntityTarget target, Holder<Attribute> attribute)
    {
        return new TargetedAttributeValueProvider(target, attribute);
    }

    @Override
    public float getFloat(LootContext ctx)
    {
        return (float) LimaEntityUtil.getAttributeValueSafe(ctx.getParamOrNull(target.getParam()), attribute);
    }

    @Override
    public LootNumberProviderType getType()
    {
        return LimaCoreLootRegistries.TARGETED_ATTRIBUTE_VALUE_NUMBER_PROVIDER.get();
    }
}