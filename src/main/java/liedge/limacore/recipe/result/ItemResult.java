package liedge.limacore.recipe.result;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import liedge.limacore.data.EmptyFieldMapCodec;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.network.LimaStreamCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.function.UnaryOperator;

public interface ItemResult
{
    String MAP_CODEC_KEY = "item_results";
    MapCodec<Boolean> REQUIRED_FIELD = Codec.BOOL.optionalFieldOf("required", true);

    Codec<ItemResult> CODEC = ItemResultType.CODEC.dispatchWithInline(ConstantItemResult.class, ConstantItemResult.INLINE_CODEC, ItemResult::getType, ItemResultType::getCodec);
    StreamCodec<RegistryFriendlyByteBuf, ItemResult> STREAM_CODEC = ItemResultType.STREAM_CODEC.dispatch(ItemResult::getType, ItemResultType::getStreamCodec);

    MapCodec<List<ItemResult>> LIST_UNIT_MAP_CODEC = EmptyFieldMapCodec.emptyListField(MAP_CODEC_KEY);
    StreamCodec<RegistryFriendlyByteBuf, List<ItemResult>> LIST_UNIT_STREAM_CODEC = StreamCodec.unit(List.of());

    Comparator<ItemResult> REQUIRED_FIRST = Comparator.comparing(ItemResult::requiredOutput).reversed();

    static MapCodec<List<ItemResult>> listMapCodec(int min, int max)
    {
        UnaryOperator<List<ItemResult>> sortingFunction = unsorted ->
        {
            if (unsorted.size() < 2 || unsorted.stream().allMatch(ItemResult::requiredOutput)) return unsorted;

            // We cannot guarantee a mutable input
            ObjectList<ItemResult> sorted = new ObjectArrayList<>(unsorted);
            sorted.sort(REQUIRED_FIRST);
            return ObjectLists.unmodifiable(sorted);
        };

        return LimaCoreCodecs.autoOptionalListField(CODEC, MAP_CODEC_KEY, min, max).xmap(sortingFunction, sortingFunction);
    }

    static StreamCodec<RegistryFriendlyByteBuf, List<ItemResult>> listStreamCodec(int min, int max)
    {
        return STREAM_CODEC.apply(LimaStreamCodecs.asClampedList(min, max));
    }

    boolean requiredOutput();

    Item getItem();

    ItemStack getMaximumResult();

    ItemStack generateResult(RandomSource random);

    ItemResultType getType();

    // For JEI GUI use on specialized subtypes. Override as needed
    default ItemStack getGuiPreviewResult()
    {
        return getMaximumResult();
    }

    default float resultChance()
    {
        return 1f;
    }

    default int minimumCount()
    {
        return maximumCount();
    }

    default int maximumCount()
    {
        return getMaximumResult().getCount();
    }
}