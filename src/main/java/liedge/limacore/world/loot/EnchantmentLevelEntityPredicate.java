package liedge.limacore.world.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import liedge.limacore.util.LimaEntityUtil;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record EnchantmentLevelEntityPredicate(Holder<Enchantment> enchantment, int minLevel, int maxLevel) implements EntitySubPredicate
{
    private static final Codec<Integer> LEVELS_CODEC = Codec.intRange(1, 255);
    public static final MapCodec<EnchantmentLevelEntityPredicate> CODEC = RecordCodecBuilder.<EnchantmentLevelEntityPredicate>mapCodec(instance -> instance.group(
            Enchantment.CODEC.fieldOf("enchantment").forGetter(EnchantmentLevelEntityPredicate::enchantment),
            LEVELS_CODEC.optionalFieldOf("min_level", 1).forGetter(EnchantmentLevelEntityPredicate::minLevel),
            LEVELS_CODEC.optionalFieldOf("max_level", 255).forGetter(EnchantmentLevelEntityPredicate::maxLevel))
            .apply(instance, EnchantmentLevelEntityPredicate::new))
            .validate(predicate -> {
                if (predicate.minLevel > predicate.maxLevel) return DataResult.error(() -> "Enchantment sub-predicate minimum level can't be higher than max level. Min: " + predicate.minLevel + ", max: " + predicate.maxLevel);
                return DataResult.success(predicate);
            });

    public static EnchantmentLevelEntityPredicate atLeast(Holder<Enchantment> enchantment, int minLevel)
    {
        return new EnchantmentLevelEntityPredicate(enchantment, minLevel, 255);
    }

    public static EnchantmentLevelEntityPredicate atMost(Holder<Enchantment> enchantment, int maxLevel)
    {
        return new EnchantmentLevelEntityPredicate(enchantment, 1, maxLevel);
    }

    @Override
    public MapCodec<? extends EntitySubPredicate> codec()
    {
        return LimaCoreLootRegistries.ENCHANTMENT_LEVEL_ENTITY_PREDICATE.get();
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position)
    {
        int enchantmentLevel = LimaEntityUtil.getEnchantmentLevel(entity, enchantment);
        return enchantmentLevel >= minLevel && enchantmentLevel <= maxLevel;
    }
}