package liedge.limacore.world.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.registry.LimaCoreLootRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class AddItemLootModifier extends LootModifier
{
    public static final MapCodec<AddItemLootModifier> MODIFIER_CODEC = RecordCodecBuilder.mapCodec(instance -> codecStart(instance)
            .and(ItemStack.CODEC.fieldOf("added_item").forGetter(o -> o.addedStack))
            .apply(instance, AddItemLootModifier::new));

    public static LootModifierBuilder.SimpleBuilder<AddItemLootModifier> addSingleStack(ItemStack stack)
    {
        return new LootModifierBuilder.SimpleBuilder<>(conditions -> new AddItemLootModifier(conditions, stack));
    }

    public static LootModifierBuilder.SimpleBuilder<AddItemLootModifier> addSingleStack(ItemLike itemLike, int count)
    {
        return addSingleStack(new ItemStack(itemLike, count));
    }

    public static LootModifierBuilder.SimpleBuilder<AddItemLootModifier> addSingleStack(ItemLike itemLike)
    {
        return addSingleStack(itemLike, 1);
    }

    private final ItemStack addedStack;

    private AddItemLootModifier(LootItemCondition[] conditions, ItemStack addedStack)
    {
        super(conditions);
        this.addedStack = addedStack;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
    {
        generatedLoot.add(addedStack.copy());
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec()
    {
        return LimaCoreLootRegistries.ADD_ITEM_MODIFIER.get();
    }
}