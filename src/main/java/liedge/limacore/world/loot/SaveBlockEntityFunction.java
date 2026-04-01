package liedge.limacore.world.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.blockentity.LimaBlockEntity;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Set;

public final class SaveBlockEntityFunction extends LootItemConditionalFunction
{
    public static final MapCodec<SaveBlockEntityFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).apply(instance, SaveBlockEntityFunction::new));

    public static Builder<?> saveBlockEntityData()
    {
        return simpleBuilder(SaveBlockEntityFunction::new);
    }

    private SaveBlockEntityFunction(List<LootItemCondition> conditions)
    {
        super(conditions);
    }

    @Override
    public MapCodec<? extends LootItemConditionalFunction> codec()
    {
        return CODEC;
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context)
    {
        BlockEntity blockEntity = context.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof LimaBlockEntity limaBE)
        {
            limaBE.saveToItemStack(stack, context.getLevel().registryAccess());
        }

        return stack;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams()
    {
        return Set.of(LootContextParams.BLOCK_ENTITY);
    }
}