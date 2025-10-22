package liedge.limacore.recipe.result;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record ConstantItemResult(ItemStack item, boolean requiredOutput) implements ItemResult
{
    static final Codec<ConstantItemResult> INLINE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LimaCoreCodecs.ITEM_STACK_MAP_CODEC.forGetter(ConstantItemResult::item),
            REQUIRED_FIELD.forGetter(ConstantItemResult::requiredOutput))
            .apply(instance, ConstantItemResult::new));
    static final MapCodec<ConstantItemResult> CODEC = INLINE_CODEC.fieldOf("item");

    @Override
    public Item getItem()
    {
        return item.getItem();
    }

    @Override
    public ItemStack getMaximumResult()
    {
        return item;
    }

    @Override
    public ItemStack generateResult(RandomSource random)
    {
        return item.copy();
    }

    @Override
    public ItemResultType getType()
    {
        return ItemResultType.CONSTANT_RESULT;
    }
}