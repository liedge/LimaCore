package liedge.limacore.recipe.result;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public final class VariableCountItemResult implements ItemResult
{
    private static DataResult<VariableCountItemResult> validate(VariableCountItemResult value)
    {
        if (value.maxCount > value.minCount)
            return DataResult.success(value);
        else
            return DataResult.error(() -> String.format("Variable item result max count (%s) must be higher than min count (%s).", value.maxCount, value.minCount));
    }

    static final MapCodec<VariableCountItemResult> CODEC = RecordCodecBuilder.<VariableCountItemResult>mapCodec(instance -> instance.group(
            ItemStack.ITEM_NON_AIR_CODEC.fieldOf("item").forGetter(o -> o.holder),
            LimaCoreCodecs.ITEM_COMPONENTS_FIELD.forGetter(o -> o.components),
            Codec.intRange(0, Item.ABSOLUTE_MAX_STACK_SIZE).fieldOf("min_count").forGetter(o -> o.minCount),
            Codec.intRange(0, Item.ABSOLUTE_MAX_STACK_SIZE).fieldOf("max_count").forGetter(o -> o.maxCount),
            REQUIRED_FIELD.forGetter(o -> o.required))
            .apply(instance, VariableCountItemResult::new))
            .validate(VariableCountItemResult::validate);

    private final Holder<Item> holder;
    private final DataComponentPatch components;
    private final int minCount;
    private final int maxCount;
    private final boolean required;

    private ItemStack maximumResult;
    private ItemStack guiPreviewResult;

    public VariableCountItemResult(Holder<Item> holder, DataComponentPatch components, int minCount, int maxCount, boolean required)
    {
        this.holder = holder;
        this.components = components;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.required = required;
    }

    @SuppressWarnings("deprecation")
    public VariableCountItemResult(ItemLike itemLike, DataComponentPatch components, int minCount, int maxCount, boolean required)
    {
        this(itemLike.asItem().builtInRegistryHolder(), components, minCount, maxCount, required);
    }

    public VariableCountItemResult(ItemStack stack, int minCount, int maxCount, boolean required)
    {
        this(stack.getItemHolder(), stack.getComponentsPatch(), minCount, maxCount, required);
    }

    @Override
    public boolean requiredOutput()
    {
        return required;
    }

    @Override
    public Item getItem()
    {
        return holder.value();
    }

    @Override
    public ItemStack getMaximumResult()
    {
        if (maximumResult == null) maximumResult = create(maxCount);
        return maximumResult;
    }

    @Override
    public ItemStack generateResult(RandomSource random)
    {
        int count = random.nextIntBetweenInclusive(minCount, maxCount);
        return create(count);
    }

    @Override
    public ItemResultType getType()
    {
        return ItemResultType.VARIABLE_COUNT;
    }

    @Override
    public ItemStack getGuiPreviewResult()
    {
        if (guiPreviewResult == null) guiPreviewResult = create(1);
        return guiPreviewResult;
    }

    @Override
    public int minimumCount()
    {
        return minCount;
    }

    @Override
    public int maximumCount()
    {
        return maxCount;
    }

    private ItemStack create(int count)
    {
        return new ItemStack(holder, count, components);
    }
}