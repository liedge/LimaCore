package liedge.limacore.recipe.result;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.EmptyFieldMapCodec;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public final class ItemResult extends StackBaseResult<Item, ItemStack>
{
    public static final Codec<ItemResult> CODEC = codec(ItemStack.ITEM_NON_AIR_CODEC, "count", Item.ABSOLUTE_MAX_STACK_SIZE, ItemResult::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemResult> STREAM_CODEC = streamCodec(LimaStreamCodecs.ITEM_HOLDER, ItemResult::new);
    public static final String MAP_CODEC_KEY = "item_results";
    public static final MapCodec<List<ItemResult>> LIST_UNIT_MAP_CODEC = EmptyFieldMapCodec.emptyListField(MAP_CODEC_KEY);

    public static MapCodec<List<ItemResult>> listMapCodec(int min, int max)
    {
        return createListMapCodec(CODEC, MAP_CODEC_KEY, min, max);
    }

    public static StreamCodec<RegistryFriendlyByteBuf, List<ItemResult>> listStreamCodec(int min, int max)
    {
        return STREAM_CODEC.apply(LimaStreamCodecs.asClampedList(min, max));
    }

    public static ItemResult create(Holder<Item> base, @Nullable DataComponentPatch components, ResultCount count, float chance, ResultPriority priority)
    {
        return new ItemResult(base, Objects.requireNonNullElse(components, DataComponentPatch.EMPTY), count, chance, priority);
    }

    public static ItemResult create(ItemLike itemLike, @Nullable DataComponentPatch components, ResultCount count, float chance, ResultPriority priority)
    {
        return create(LimaRegistryUtil.getHolder(itemLike), components, count, chance, priority);
    }

    public static ItemResult create(ItemStack stack, float chance, ResultPriority priority, @Nullable ResultCount count)
    {
        count = count != null ? count : ResultCount.exactly(stack.getCount());
        return create(stack.getItem(), stack.getComponentsPatch(), count, chance, priority);
    }

    public static ItemResult create(ItemStack stack, float chance, ResultPriority priority)
    {
        return create(stack, chance, priority, null);
    }

    private ItemResult(Holder<Item> item, DataComponentPatch components, ResultCount count, float chance, ResultPriority priority)
    {
        super(item, components, count, chance, priority);
    }

    @Override
    protected ItemStack createStack(int stackSize)
    {
        return new ItemStack(base, stackSize, components);
    }

    @Override
    protected ItemStack getEmptyStack()
    {
        return ItemStack.EMPTY;
    }

    public Item getItem()
    {
        return base.value();
    }
}