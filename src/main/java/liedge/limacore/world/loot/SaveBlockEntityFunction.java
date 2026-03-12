package liedge.limacore.world.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Set;

/**
 * Runs {@link BlockEntity#saveToItem(ItemStack, HolderLookup.Provider)} on the loot item stack. Only
 * copies data if the loot item is a block item and that block is contained within {@link BlockEntityType#getValidBlocks()}.
 */
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
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType()
    {
        return LimaCoreLootRegistries.SAVE_BLOCK_ENTITY.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context)
    {
        BlockEntity blockEntity = context.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        if (stack.getItem() instanceof BlockItem blockItem
                && blockEntity != null
                && blockEntity.getType().getValidBlocks().contains(blockItem.getBlock()))
        {
            CustomData data = CustomData.of(blockEntity.saveCustomOnly(context.getLevel().registryAccess()));
            stack.set(DataComponents.CUSTOM_DATA, data);
        }

        return stack;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams()
    {
        return Set.of(LootContextParams.BLOCK_ENTITY);
    }
}