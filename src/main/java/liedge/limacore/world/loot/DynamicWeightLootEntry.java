package liedge.limacore.world.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class DynamicWeightLootEntry extends LootPoolEntryContainer
{
    public static final MapCodec<DynamicWeightLootEntry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviders.CODEC.fieldOf("dynamic_weight").forGetter(o -> o.dynamicWeight),
            Codec.BOOL.optionalFieldOf("replace_weight", true).forGetter(o -> o.replaceWeight),
            LootPoolEntries.CODEC.fieldOf("child").forGetter(o -> o.child))
            .and(commonFields(instance).t1())
            .apply(instance, DynamicWeightLootEntry::new));

    private final NumberProvider dynamicWeight;
    private final boolean replaceWeight;
    private final LootPoolEntryContainer child;

    public static Builder dynamicWeight(LootPoolEntryContainer.Builder<?> childBuilder)
    {
        return new Builder(childBuilder.build());
    }

    public static Builder dynamicWeightItem(ItemLike item)
    {
        return dynamicWeight(LootItem.lootTableItem(item));
    }

    public static Builder dynamicWeightItem(ItemLike item, int baseWeight)
    {
        return dynamicWeight(LootItem.lootTableItem(item).setWeight(baseWeight));
    }

    private DynamicWeightLootEntry(NumberProvider dynamicWeight, boolean replaceWeight, LootPoolEntryContainer child, List<LootItemCondition> conditions)
    {
        super(conditions);
        this.dynamicWeight = dynamicWeight;
        this.replaceWeight = replaceWeight;
        this.child = child;
    }

    @Override
    public void validate(ValidationContext validationContext)
    {
        super.validate(validationContext);
        child.validate(validationContext.forChild(".child_entry"));
    }

    @Override
    public LootPoolEntryType getType()
    {
        return LimaCoreLootRegistries.DYNAMIC_WEIGHT_LOOT_ENTRY.get();
    }

    @Override
    public boolean expand(LootContext ctx, Consumer<LootPoolEntry> entryConsumer)
    {
        if (canRun(ctx))
        {
            int dynamicWeight = this.dynamicWeight.getInt(ctx);

            return child.expand(ctx, original -> entryConsumer.accept(new LootPoolEntry()
            {
                @Override
                public int getWeight(float luck)
                {
                    return replaceWeight ? dynamicWeight : original.getWeight(luck) + dynamicWeight;
                }

                @Override
                public void createItemStack(Consumer<ItemStack> stackConsumer, LootContext lootContext)
                {
                    original.createItemStack(stackConsumer, lootContext);
                }
            }));
        }

        return false;
    }

    public static class Builder extends LootPoolEntryContainer.Builder<Builder>
    {
        private final LootPoolEntryContainer child;

        private NumberProvider dynamicWeight;
        private boolean replaceWeight = true;

        public Builder(LootPoolEntryContainer child)
        {
            this.child = child;
        }

        public Builder setReplaceWeight(boolean replaceWeight)
        {
            this.replaceWeight = replaceWeight;
            return this;
        }

        public Builder setDynamicWeight(NumberProvider dynamicWeight)
        {
            this.dynamicWeight = dynamicWeight;
            return this;
        }

        @Override
        protected Builder getThis()
        {
            return this;
        }

        @Override
        public LootPoolEntryContainer build()
        {
            Objects.requireNonNull(dynamicWeight, "Dynamic weight not set.");
            return new DynamicWeightLootEntry(dynamicWeight, replaceWeight, child, getConditions());
        }
    }
}