package liedge.limacore.world.loot.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.util.LimaEntityUtil;
import net.minecraft.core.Holder;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.Set;

public record EntityAttributeValue(LootContext.EntityTarget target, Holder<Attribute> attribute, boolean base) implements NumberProvider
{
    public static final MapCodec<EntityAttributeValue> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LootContext.EntityTarget.CODEC.fieldOf("target").forGetter(EntityAttributeValue::target),
            Attribute.CODEC.fieldOf("attribute").forGetter(EntityAttributeValue::attribute),
            Codec.BOOL.optionalFieldOf("base", false).forGetter(EntityAttributeValue::base))
            .apply(instance, EntityAttributeValue::new));

    public static NumberProvider totalValue(LootContext.EntityTarget target, Holder<Attribute> attribute)
    {
        return new EntityAttributeValue(target, attribute, false);
    }

    public static NumberProvider baseValue(LootContext.EntityTarget target, Holder<Attribute> attribute)
    {
        return new EntityAttributeValue(target, attribute, true);
    }

    @Override
    public float getFloat(LootContext ctx)
    {
        Entity entity = ctx.getOptionalParameter(target.contextParam());
        double value = base ? LimaEntityUtil.getAttributeBaseValueSafe(entity, attribute) : LimaEntityUtil.getAttributeValueSafe(entity, attribute);
        return (float) value;
    }

    @Override
    public MapCodec<? extends NumberProvider> codec()
    {
        return CODEC;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams()
    {
        return Set.of(target.contextParam());
    }
}