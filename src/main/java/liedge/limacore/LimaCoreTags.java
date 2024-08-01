package liedge.limacore;

import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import static liedge.limacore.LimaCore.RESOURCES;

public final class LimaCoreTags
{
    private LimaCoreTags() {}

    public static final class DamageTypes
    {
        private DamageTypes() {}

        public static final TagKey<DamageType> IGNORES_KNOCKBACK_RESISTANCE = RESOURCES.damageTypeTag("ignores_knockback_resistance");
    }
}