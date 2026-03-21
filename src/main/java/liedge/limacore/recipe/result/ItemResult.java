package liedge.limacore.recipe.result;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.EmptyFieldMapCodec;
import liedge.limacore.network.LimaStreamCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.transfer.item.ItemResource;

import javax.annotation.Nullable;
import java.util.List;

public final class ItemResult extends ResourceResult<ItemResource>
{
    public static final Codec<ItemResult> CODEC = codec(ItemResource.CODEC.fieldOf("item"), Item.ABSOLUTE_MAX_STACK_SIZE, ItemResult::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemResult> STREAM_CODEC = streamCodec(ItemResource.STREAM_CODEC, ItemResult::new);
    public static final String MAP_CODEC_KEY = "item_results";
    public static final MapCodec<List<ItemResult>> LIST_UNIT_MAP_CODEC = EmptyFieldMapCodec.emptyListField(MAP_CODEC_KEY);

    public static MapCodec<List<ItemResult>> listMapCodec(int min, int max)
    {
        return listMapCodec(CODEC, MAP_CODEC_KEY, min, max);
    }

    public static StreamCodec<RegistryFriendlyByteBuf, List<ItemResult>> listStreamCodec(int min, int max)
    {
        return STREAM_CODEC.apply(LimaStreamCodecs.asClampedList(min, max));
    }

    public static ItemResult create(ItemResource resource, ResultCount count, float chance, ResultPriority priority)
    {
        return new ItemResult(resource, count, chance, priority);
    }

    public static ItemResult create(ItemStack stack, float chance, ResultPriority priority, @Nullable ResultCount count)
    {
        ItemResource resource = ItemResource.of(stack);
        count = count != null ? count : ResultCount.exactly(stack.getCount());
        return create(resource, count, chance, priority);
    }

    public static ItemResult create(ItemStack stack, float chance, ResultPriority priority)
    {
        return create(stack, chance, priority, null);
    }

    public static ItemResult create(ItemLike itemLike, ResultCount count, float chance, ResultPriority priority)
    {
        return create(ItemResource.of(itemLike), count, chance, priority);
    }

    private ItemResult(ItemResource resource, ResultCount count, float chance, ResultPriority priority)
    {
        super(resource, count, chance, priority);
    }

    public Item getItem()
    {
        return resource.getItem();
    }
}