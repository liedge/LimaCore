package liedge.limacore.recipe.result;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record RandomChanceItemResult(ItemStack item, float resultChance, boolean requiredOutput) implements ItemResult
{
    static final MapCodec<RandomChanceItemResult> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(RandomChanceItemResult::item),
            LimaCoreCodecs.floatOpenRange(0f, 1f).fieldOf("chance").forGetter(RandomChanceItemResult::resultChance),
            REQUIRED_FIELD.forGetter(RandomChanceItemResult::requiredOutput))
            .apply(instance, RandomChanceItemResult::new));
    static final StreamCodec<RegistryFriendlyByteBuf, RandomChanceItemResult> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, RandomChanceItemResult::item,
            ByteBufCodecs.FLOAT, RandomChanceItemResult::resultChance,
            ByteBufCodecs.BOOL, RandomChanceItemResult::requiredOutput,
            RandomChanceItemResult::new);

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
        return random.nextFloat() < resultChance ? item.copy() : ItemStack.EMPTY;
    }

    @Override
    public ItemResultType getType()
    {
        return ItemResultType.RANDOM_CHANCE;
    }
}