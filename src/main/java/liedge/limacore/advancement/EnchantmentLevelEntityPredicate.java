package liedge.limacore.advancement;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.util.LimaEntityUtil;
import net.minecraft.advancements.criterion.EntitySubPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record EnchantmentLevelEntityPredicate(Holder<Enchantment> enchantment, MinMaxBounds.Ints levels) implements EntitySubPredicate
{
    public static final MapCodec<EnchantmentLevelEntityPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Enchantment.CODEC.fieldOf("enchantment").forGetter(EnchantmentLevelEntityPredicate::enchantment),
            MinMaxBounds.Ints.CODEC.fieldOf("levels").forGetter(EnchantmentLevelEntityPredicate::levels))
            .apply(instance, EnchantmentLevelEntityPredicate::new));

    public static EnchantmentLevelEntityPredicate create(Holder<Enchantment> enchantment, MinMaxBounds.Ints levels)
    {
        return new EnchantmentLevelEntityPredicate(enchantment, levels);
    }

    @Override
    public MapCodec<? extends EntitySubPredicate> codec()
    {
        return CODEC;
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position)
    {
        int enchantmentLevel = LimaEntityUtil.getEnchantmentLevel(entity, enchantment);
        return levels.matches(enchantmentLevel);
    }
}