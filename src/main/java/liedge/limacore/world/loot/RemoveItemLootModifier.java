package liedge.limacore.world.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.registry.LimaCoreLootRegistries;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class RemoveItemLootModifier extends LootModifier
{
    public static final MapCodec<RemoveItemLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> codecStart(instance)
            .and(ItemPredicate.CODEC.fieldOf("predicate").forGetter(o -> o.predicate)).apply(instance, RemoveItemLootModifier::new));

    public static LootModifierBuilder.SimpleBuilder<RemoveItemLootModifier> removeMatchingDrops(ItemPredicate predicate)
    {
        return new LootModifierBuilder.SimpleBuilder<>(conditions -> new RemoveItemLootModifier(conditions, predicate));
    }

    public static LootModifierBuilder.SimpleBuilder<RemoveItemLootModifier> removeMatchingDrops(ItemPredicate.Builder builder)
    {
        return removeMatchingDrops(builder.build());
    }

    private final ItemPredicate predicate;

    private RemoveItemLootModifier(LootItemCondition[] conditions, ItemPredicate predicate)
    {
        super(conditions);
        this.predicate = predicate;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
    {
        generatedLoot.removeIf(predicate);
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec()
    {
        return LimaCoreLootRegistries.REMOVE_ITEM_MODIFIER.get();
    }
}