package liedge.limacore.world.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.List;
import java.util.function.Predicate;

public final class RemoveItemLootModifier extends LootModifier
{
    public static final MapCodec<RemoveItemLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> codecStart(instance)
            .and(LimaCoreCodecs.singleOrPluralNonEmpty(ItemPredicate.CODEC, "item_predicate").forGetter(o -> o.itemPredicates))
            .apply(instance, RemoveItemLootModifier::new));

    public static Builder removeDrops()
    {
        return new Builder();
    }

    private final List<ItemPredicate> itemPredicates;
    private final Predicate<ItemStack> combinedPredicate;

    private RemoveItemLootModifier(LootItemCondition[] conditions, List<ItemPredicate> itemPredicates)
    {
        super(conditions);
        this.itemPredicates = itemPredicates;
        this.combinedPredicate = Util.allOf(itemPredicates);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
    {
        generatedLoot.removeIf(combinedPredicate);
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec()
    {
        return LimaCoreLootRegistries.REMOVE_ITEM_MODIFIER.get();
    }

    public static final class Builder extends LootModifierBuilder<RemoveItemLootModifier, Builder>
    {
        private final List<ItemPredicate> predicates = new ObjectArrayList<>();

        private Builder() { }

        public Builder itemMatches(ItemPredicate.Builder predicate)
        {
            predicates.add(predicate.build());
            return this;
        }

        @Override
        protected RemoveItemLootModifier createModifier(LootItemCondition[] conditions)
        {
            return new RemoveItemLootModifier(conditions, predicates);
        }
    }
}