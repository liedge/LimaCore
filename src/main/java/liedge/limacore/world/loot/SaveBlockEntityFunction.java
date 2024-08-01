package liedge.limacore.world.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.LimaCoreLootRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Set;

public class SaveBlockEntityFunction extends LootItemConditionalFunction
{
    public static final MapCodec<SaveBlockEntityFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).apply(instance, SaveBlockEntityFunction::new));

    public static Builder<?> saveBlockEntityData()
    {
        return simpleBuilder(SaveBlockEntityFunction::new);
    }

    private SaveBlockEntityFunction(List<LootItemCondition> conditions)
    {
        super(conditions);
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType()
    {
        return LimaCoreLootRegistries.SAVE_BLOCK_ENTITY.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context)
    {
        BlockEntity blockEntity = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        if (blockEntity != null)
        {
            blockEntity.saveToItem(stack, context.getLevel().registryAccess());
        }

        return stack;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return Set.of(LootContextParams.BLOCK_ENTITY);
    }
}