package liedge.limacore.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.EmptyFieldMapCodec;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.network.LimaStreamCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ItemResult(ItemStack item, float chance)
{
    private static final String MAP_CODEC_KEY = "item_results";
    public static final Codec<ItemResult> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LimaCoreCodecs.ITEM_STACK_MAP_CODEC.forGetter(ItemResult::item),
            LimaCoreCodecs.floatOpenStartRange(0f, 1f).optionalFieldOf("chance", 1f).forGetter(ItemResult::chance))
            .apply(instance, ItemResult::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemResult> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            ItemResult::item,
            ByteBufCodecs.FLOAT,
            ItemResult::chance,
            ItemResult::new);
    public static final MapCodec<List<ItemResult>> LIST_UNIT_MAP_CODEC = EmptyFieldMapCodec.emptyListField(MAP_CODEC_KEY);
    public static final StreamCodec<RegistryFriendlyByteBuf, List<ItemResult>> LIST_UNIT_STREAM_CODEC = StreamCodec.unit(List.of());

    public static MapCodec<List<ItemResult>> listMapCodec(int min, int max)
    {
        return LimaCoreCodecs.smartSizedListField(CODEC, MAP_CODEC_KEY, min, max);
    }

    public static StreamCodec<RegistryFriendlyByteBuf, List<ItemResult>> listStreamCodec(int min, int max)
    {
        return STREAM_CODEC.apply(LimaStreamCodecs.asClampedList(min, max));
    }

    public ItemResult(ItemStack item)
    {
        this(item, 1f);
    }

    public ItemStack generateResult(RandomSource random)
    {
        if (chance == 1f || random.nextFloat() <= chance)
            return item.copy();
        else
            return ItemStack.EMPTY;
    }
}