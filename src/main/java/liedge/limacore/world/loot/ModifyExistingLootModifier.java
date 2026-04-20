package liedge.limacore.world.loot;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;

public abstract class ModifyExistingLootModifier extends LootModifier
{
    protected static <T extends ModifyExistingLootModifier> Products.P3<RecordCodecBuilder.Mu<T>, LootItemCondition[], Integer, List<ItemPredicate>> commonFields(RecordCodecBuilder.Instance<T> instance)
    {
        return codecStart(instance).and(ItemPredicate.CODEC.listOf().fieldOf("applies_to").forGetter(o -> o.itemPredicates));
    }

    protected final List<ItemPredicate> itemPredicates;
    private final Predicate<ItemStack> itemTest;

    protected ModifyExistingLootModifier(LootItemCondition[] conditions, int priority, List<ItemPredicate> itemPredicates)
    {
        super(conditions, priority);
        this.itemPredicates = itemPredicates;
        this.itemTest = Util.allOf(itemPredicates);
    }

    protected abstract ItemStack modifyStack(ItemStack original, LootContext context);

    @Override
    protected final ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
    {
        ListIterator<ItemStack> iterator = generatedLoot.listIterator();

        while (iterator.hasNext())
        {
            ItemStack stack = iterator.next();

            if (itemTest.test(stack))
            {
                stack = modifyStack(stack, context);
                if (stack.isEmpty())
                    iterator.remove();
                else
                    iterator.set(stack);
            }
        }

        return generatedLoot;
    }

    public static final class Builder<M extends ModifyExistingLootModifier> extends LootModifierBuilder<M, Builder<M>>
    {
        private final List<ItemPredicate> itemPredicates = new ObjectArrayList<>();
        private final Function3<LootItemCondition[], Integer, List<ItemPredicate>, M> factory;

        public Builder(Function3<LootItemCondition[], Integer, List<ItemPredicate>, M> factory)
        {
            this.factory = factory;
        }

        public Builder<M> appliesTo(ItemPredicate predicate)
        {
            itemPredicates.add(predicate);
            return this;
        }

        public Builder<M> appliesTo(ItemPredicate.Builder builder)
        {
            return appliesTo(builder.build());
        }

        @Override
        protected M createModifier(LootItemCondition[] conditions, int priority)
        {
            return factory.apply(conditions, priority, itemPredicates);
        }
    }
}