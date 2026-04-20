package liedge.limacore.world.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;

import java.util.List;
import java.util.function.BiFunction;

public final class ApplyFunctionsLootModifier extends ModifyExistingLootModifier
{
    public static final MapCodec<ApplyFunctionsLootModifier> CODEC = RecordCodecBuilder.mapCodec(i -> commonFields(i)
            .and(LimaCoreCodecs.singleOrPluralNonEmpty(LootItemFunctions.ROOT_CODEC, "function").forGetter(o -> o.functions))
            .apply(i, ApplyFunctionsLootModifier::new));

    private final List<LootItemFunction> functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> composedFunction;

    private ApplyFunctionsLootModifier(LootItemCondition[] conditions, int priority, List<ItemPredicate> itemPredicates, List<LootItemFunction> functions)
    {
        super(conditions, priority, itemPredicates);
        this.functions = functions;
        this.composedFunction = LootItemFunctions.compose(functions);
    }

    @Override
    protected ItemStack modifyStack(ItemStack original, LootContext context)
    {
        return composedFunction.apply(original, context);
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec()
    {
        return CODEC;
    }
}