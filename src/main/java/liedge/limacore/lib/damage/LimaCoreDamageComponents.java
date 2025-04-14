package liedge.limacore.lib.damage;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;
import java.util.Set;

import static liedge.limacore.LimaCore.RESOURCES;

public final class LimaCoreDamageComponents
{
    private LimaCoreDamageComponents() {}

    public static final DataComponentType<Set<TagKey<DamageType>>> DYNAMIC_TAGS = RESOURCES.transientDataComponent("dynamic_tags");
    public static final DataComponentType<List<ReductionModifier>> REDUCTION_MODIFIERS = RESOURCES.transientDataComponent("reduction_modifiers");
    public static final DataComponentType<List<ItemAttributeModifiers.Entry>> ATTRIBUTE_MODIFIERS = RESOURCES.transientDataComponent("attribute_modifiers");
}