package liedge.limacore.world.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public final class AddItemLootModifier extends LootModifier
{
    public static final MapCodec<AddItemLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> codecStart(instance)
            .and(ItemStack.CODEC.fieldOf("item").forGetter(o -> o.item))
            .apply(instance, AddItemLootModifier::new));

    public static LootModifierBuilder.SimpleBuilder<AddItemLootModifier> addItem(ItemStack stack)
    {
        return new LootModifierBuilder.SimpleBuilder<>(conditions -> new AddItemLootModifier(conditions, stack));
    }

    public static LootModifierBuilder.SimpleBuilder<AddItemLootModifier> addItem(ItemLike item, int count)
    {
        return addItem(new ItemStack(item, count));
    }

    public static LootModifierBuilder.SimpleBuilder<AddItemLootModifier> addItem(ItemLike item)
    {
        return addItem(item, 1);
    }

    private final ItemStack item;

    private AddItemLootModifier(LootItemCondition[] conditions, ItemStack item)
    {
        super(conditions);
        this.item = item;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
    {
        generatedLoot.add(item.copy());
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec()
    {
        return LimaCoreLootRegistries.ADD_ITEM_MODIFIER.get();
    }
}