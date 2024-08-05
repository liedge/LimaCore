package liedge.limacore.world.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import liedge.limacore.registry.LimaCoreLootRegistries;
import liedge.limacore.util.LimaCoreUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.Consumer;

public class EnchantmentBasedWeightLootItem extends LootPoolEntryContainer
{
    public static final MapCodec<EnchantmentBasedWeightLootItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("item").forGetter(o -> o.item),
                    Codec.INT.fieldOf("base_weight").forGetter(o -> o.baseWeight),
                    Enchantment.CODEC.fieldOf("enchantment").forGetter(o -> o.enchantment),
                    Codec.INT.fieldOf("weight_per_level").forGetter(o -> o.weightPerLevel))
            .and(commonFields(instance).t1())
            .and(LootItemFunctions.ROOT_CODEC.listOf().optionalFieldOf("functions", List.of()).forGetter(o -> o.functions))
            .apply(instance, EnchantmentBasedWeightLootItem::new));

    public static Builder enchantedWeightLootItem(ItemLike item, int baseWeight, Holder<Enchantment> enchantment, int weightPerLevel)
    {
        return new Builder(item, baseWeight, enchantment, weightPerLevel);
    }

    private final Holder<Item> item;
    private final int baseWeight;
    private final Holder<Enchantment> enchantment;
    private final int weightPerLevel;
    private final List<LootItemFunction> functions;

    private EnchantmentBasedWeightLootItem(Holder<Item> item, int baseWeight, Holder<Enchantment> enchantment, int weightPerLevel, List<LootItemCondition> conditions, List<LootItemFunction> functions)
    {
        super(conditions);
        this.item = item;
        this.baseWeight = baseWeight;
        this.enchantment = enchantment;
        this.weightPerLevel = weightPerLevel;
        this.functions = functions;
    }

    @Override
    public LootPoolEntryType getType()
    {
        return LimaCoreLootRegistries.ENCHANTMENT_BASED_WEIGHT.get();
    }

    @Override
    public boolean expand(LootContext ctx, Consumer<LootPoolEntry> entryConsumer)
    {
        if (canRun(ctx))
        {
            LivingEntity attacker = LimaCoreUtil.castOrNull(LivingEntity.class, ctx.getParamOrNull(LootContextParams.ATTACKING_ENTITY));

            if (attacker != null)
            {
                int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(enchantment, attacker);

                entryConsumer.accept(new LootPoolEntry()
                {
                    @Override
                    public int getWeight(float ignoredLuck)
                    {
                        return baseWeight + (weightPerLevel * enchantmentLevel);
                    }

                    @Override
                    public void createItemStack(Consumer<ItemStack> stackConsumer, LootContext lootContext)
                    {
                        ItemStack stack = new ItemStack(item);
                        for (LootItemFunction function : functions)
                        {
                            stack = function.apply(stack, lootContext);
                        }

                        stackConsumer.accept(stack);
                    }
                });

                return true;
            }
        }

        return false;
    }

    @Override
    public void validate(ValidationContext validationContext)
    {
        super.validate(validationContext);

        for (int i = 0; i < functions.size(); i++)
        {
            functions.get(i).validate(validationContext.forChild(".functions[" + i + "]"));
        }
    }

    public static class Builder extends LootPoolEntryContainer.Builder<Builder> implements FunctionUserBuilder<Builder>
    {
        private final ObjectList<LootItemFunction> functions = new ObjectArrayList<>();
        private final Holder<Item> item;
        private final int baseWeight;
        private final Holder<Enchantment> enchantment;
        private final int weightPerLevel;

        @SuppressWarnings("deprecation")
        private Builder(ItemLike itemLike, int baseWeight, Holder<Enchantment> enchantment, int weightPerLevel)
        {
            this.item = itemLike.asItem().builtInRegistryHolder();
            this.baseWeight = baseWeight;
            this.enchantment = enchantment;
            this.weightPerLevel = weightPerLevel;
        }

        @Override
        protected Builder getThis()
        {
            return this;
        }

        @Override
        public LootPoolEntryContainer build()
        {
            return new EnchantmentBasedWeightLootItem(item, baseWeight, enchantment, weightPerLevel, getConditions(), ObjectLists.unmodifiable(functions));
        }

        @Override
        public Builder apply(LootItemFunction.Builder functionBuilder)
        {
            functions.add(functionBuilder.build());
            return this;
        }
    }
}