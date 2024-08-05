package liedge.limacore.world.loot;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.Nullable;

public abstract class LootTableDispatchLootModifier extends LootModifier
{
    protected LootTableDispatchLootModifier(LootItemCondition[] conditions)
    {
        super(conditions);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
    {
        ResourceKey<LootTable> tableKey = lookupLootTable(context);
        if (tableKey != null)
        {
            context.getResolver().get(Registries.LOOT_TABLE, tableKey).ifPresent(table -> table.value().getRandomItemsRaw(context, LootTable.createStackSplitter(context.getLevel(), generatedLoot::add)));
        }

        return generatedLoot;
    }

    @Override
    public abstract MapCodec<? extends LootTableDispatchLootModifier> codec();

    protected abstract @Nullable ResourceKey<LootTable> lookupLootTable(LootContext context);
}