package liedge.limacore.util;

import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import org.jetbrains.annotations.Nullable;

public final class LimaEntityUtil
{
    private LimaEntityUtil() {}

    public static @Nullable AttributeInstance getAttributeInstanceSafe(@Nullable Entity entity, Holder<Attribute> attribute)
    {
        if (entity instanceof LivingEntity livingEntity)
        {
            return livingEntity.getAttribute(attribute);
        }

        return null;
    }

    public static double getAttributeValueSafe(@Nullable Entity entity, Holder<Attribute> attribute)
    {
        AttributeInstance instance = getAttributeInstanceSafe(entity, attribute);
        return instance != null ? instance.getValue() : 0d;
    }

    public static double getAttributeBaseValueSafe(@Nullable Entity entity, Holder<Attribute> attribute)
    {
        AttributeInstance instance = getAttributeInstanceSafe(entity, attribute);
        return instance != null ? instance.getBaseValue() : 0d;
    }

    public static int getEntityId(@Nullable Entity entity)
    {
        return entity != null ? entity.getId() : 0;
    }

    public static boolean isEntityHostile(Entity entity)
    {
        return entity instanceof Enemy || !entity.getType().getCategory().isFriendly();
    }

    public static void customKnockbackEntity(LivingEntity entity, boolean ignoreResist, double strength, double ratioX, double ratioZ)
    {
        LivingKnockBackEvent event = CommonHooks.onLivingKnockBack(entity, (float) strength, ratioX, ratioZ);
        if (event.isCanceled()) return;

        strength = event.getStrength();
        ratioX = event.getRatioX();
        ratioZ = event.getRatioZ();

        double resist = ignoreResist ? 0 : entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        strength *= (1 - resist);
        if (strength > 0)
        {
            entity.hasImpulse = true;
            Vec3 vec3 = entity.getDeltaMovement();

            while (ratioX * ratioX + ratioZ * ratioZ < 1.0e-5f)
            {
                ratioX = (Math.random() - Math.random()) * 0.01d;
                ratioZ = (Math.random() - Math.random()) * 0.01d;
            }

            Vec3 vec31 = new Vec3(ratioX, 0.0d, ratioZ).normalize().scale(strength);
            entity.setDeltaMovement(vec3.x / 2.0d - vec31.x, entity.onGround() ? Math.min(0.4d, vec3.y / 2.0d + strength) : vec3.y, vec3.z / 2.0d - vec31.z);
        }
    }

    public static boolean isEntityUsingItem(LivingEntity entity, InteractionHand hand)
    {
        return entity.isUsingItem() &&  entity.getUsedItemHand() == hand;
    }
}