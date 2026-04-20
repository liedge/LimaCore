package liedge.limacore.world.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;

import java.util.List;

public final class RemoveItemLootModifier extends ModifyExistingLootModifier
{
    public static final MapCodec<RemoveItemLootModifier> CODEC = RecordCodecBuilder.mapCodec(i -> commonFields(i).apply(i, RemoveItemLootModifier::new));

    public static Builder<RemoveItemLootModifier> removeItems()
    {
        return new Builder<>(RemoveItemLootModifier::new);
    }

    private RemoveItemLootModifier(LootItemCondition[] conditions, int priority, List<ItemPredicate> itemPredicates)
    {
        super(conditions, priority, itemPredicates);
    }

    @Override
    protected ItemStack modifyStack(ItemStack original, LootContext context)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec()
    {
        return CODEC;
    }
}