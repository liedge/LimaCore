package liedge.limacore.world.loot;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.registry.LimaCoreDataComponents;
import liedge.limacore.registry.LimaCoreLootRegistries;
import liedge.limacore.util.LimaCoreUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;

public final class ExtraLootTableEffectLootModifier implements IGlobalLootModifier
{
    private static final ExtraLootTableEffectLootModifier INSTANCE = new ExtraLootTableEffectLootModifier();
    public static final MapCodec<ExtraLootTableEffectLootModifier> CODEC = MapCodec.unit(INSTANCE);

    public static ExtraLootTableEffectLootModifier lootTableEffectComponentModifier()
    {
        return INSTANCE;
    }

    private ExtraLootTableEffectLootModifier() {}

    @Override
    public ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
    {
        Entity killedEntity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        LivingEntity attacker = LimaCoreUtil.castOrNull(LivingEntity.class, context.getParamOrNull(LootContextParams.ATTACKING_ENTITY));
        DamageSource damageSource = context.getParamOrNull(LootContextParams.DAMAGE_SOURCE);

        if (killedEntity != null && attacker != null && damageSource != null)
        {
            EnchantmentHelper.runIterationOnItem(attacker.getWeaponItem(), (enchantment, level) -> {
                LootContext enchantedContext = Enchantment.damageContext(context.getLevel(), level, killedEntity, damageSource);
                Enchantment.applyEffects(enchantment.value().getEffects(LimaCoreDataComponents.EXTRA_LOOT_TABLE_EFFECT.get()), enchantedContext, key -> addExtraLoot(generatedLoot, enchantedContext, key));
            });
        }

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec()
    {
        return LimaCoreLootRegistries.LOOT_TABLE_ENCHANTMENT_COMPONENT_MODIFIER.get();
    }

    @SuppressWarnings("deprecation")
    private void addExtraLoot(ObjectArrayList<ItemStack> generatedLoot, LootContext context, ResourceKey<LootTable> tableKey)
    {
        context.getResolver().get(Registries.LOOT_TABLE, tableKey).ifPresent(extraLoot -> extraLoot.value().getRandomItemsRaw(context, LootTable.createStackSplitter(context.getLevel(), generatedLoot::add)));
    }
}