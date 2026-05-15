package liedge.limacore.world.loot.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Set;

public record MatchWeaponItem(ItemPredicate predicate) implements LootItemCondition
{
    public static final MapCodec<MatchWeaponItem> CODEC = ItemPredicate.CODEC.fieldOf("predicate").xmap(MatchWeaponItem::new, MatchWeaponItem::predicate);

    public static LootItemCondition.Builder matchWeapon(ItemPredicate predicate)
    {
        return () -> new MatchWeaponItem(predicate);
    }

    public static LootItemCondition.Builder matchWeapon(ItemPredicate.Builder predicate)
    {
        return matchWeapon(predicate.build());
    }

    @Override
    public MapCodec<? extends LootItemCondition> codec()
    {
        return CODEC;
    }

    @Override
    public boolean test(LootContext context)
    {
        DamageSource damageSource = context.getOptionalParameter(LootContextParams.DAMAGE_SOURCE);
        if (damageSource == null) return false;

        ItemStack stack = damageSource.getWeaponItem();
        return stack != null && predicate.test(stack);
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams()
    {
        return Set.of(LootContextParams.DAMAGE_SOURCE);
    }
}