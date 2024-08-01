package liedge.limacore.world.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.registry.LimaCoreLootRegistries;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.Map;
import java.util.Objects;

public final class EnchantmentBasedEntityTableLootModifier extends LootModifier
{
    public static final MapCodec<EnchantmentBasedEntityTableLootModifier> MODIFIER_CODEC = RecordCodecBuilder.mapCodec(instance -> codecStart(instance)
            .and(Enchantment.CODEC.fieldOf("enchantment").forGetter(o -> o.enchantment))
            .apply(instance, EnchantmentBasedEntityTableLootModifier::new));

    private final Map<EntityType<?>, ResourceKey<LootTable>> cachedKeys = new Object2ObjectOpenHashMap<>();
    private final Holder<Enchantment> enchantment;

    public static ResourceKey<LootTable> lootTableKeyForEntity(Holder<Enchantment> enchantment, EntityType<?> type)
    {
        ResourceLocation entityId = LimaRegistryUtil.getNonNullRegistryKey(type, BuiltInRegistries.ENTITY_TYPE);
        String enchantType = Objects.requireNonNull(enchantment.getKey(), "No resource key associated with enchantment").location().getPath();
        return ResourceKey.create(Registries.LOOT_TABLE, entityId.withPrefix("enchantment_drop/" + enchantType + "/"));
    }

    public EnchantmentBasedEntityTableLootModifier(LootItemCondition[] conditions, Holder<Enchantment> enchantment)
    {
        super(conditions);
        this.enchantment = enchantment;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
    {
        Entity thisEntity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        Entity attacker = context.getParamOrNull(LootContextParams.ATTACKING_ENTITY);

        if (thisEntity != null && attacker instanceof LivingEntity livingAttacker)
        {
            int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(enchantment, livingAttacker);

            if (enchantmentLevel > 0)
            {
                ResourceKey<LootTable> tableKey = cachedKeys.computeIfAbsent(thisEntity.getType(), type -> lootTableKeyForEntity(enchantment, type));
                context.getResolver().get(Registries.LOOT_TABLE, tableKey).ifPresent(table -> table.value().getRandomItemsRaw(context, LootTable.createStackSplitter(context.getLevel(), generatedLoot::add)));
            }
        }

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec()
    {
        return LimaCoreLootRegistries.ENCHANTMENT_BASED_TABLE_MODIFIER.get();
    }
}