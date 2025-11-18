package liedge.limacore.recipe.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.EmptyFieldMapCodec;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.network.LimaStreamCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.List;

public final class LimaSizedItemIngredient extends LimaSizedIngredient<Ingredient, ItemStack>
{
    public static final String MAP_CODEC_KEY = "ingredients";
    public static final Codec<LimaSizedItemIngredient> CODEC = codec(Ingredient.MAP_CODEC_NONEMPTY, "count", LimaSizedItemIngredient::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, LimaSizedItemIngredient> STREAM_CODEC = streamCodec(Ingredient.CONTENTS_STREAM_CODEC, LimaSizedItemIngredient::new);
    public static final MapCodec<List<LimaSizedItemIngredient>> LIST_UNIT_MAP_CODEC = EmptyFieldMapCodec.emptyListField(MAP_CODEC_KEY);

    public static MapCodec<List<LimaSizedItemIngredient>> listMapCodec(int minInclusive, int maxExclusive)
    {
        return LimaCoreCodecs.autoOptionalListField(CODEC, MAP_CODEC_KEY, minInclusive, maxExclusive);
    }

    public static StreamCodec<RegistryFriendlyByteBuf, List<LimaSizedItemIngredient>> listStreamCodec(int minInclusive, int maxInclusive)
    {
        return STREAM_CODEC.apply(LimaStreamCodecs.asClampedList(minInclusive, maxInclusive));
    }

    public LimaSizedItemIngredient(Ingredient ingredient, int count, float consumeChance)
    {
        super(ingredient, count, consumeChance);
    }

    public LimaSizedItemIngredient(Ingredient ingredient, int count)
    {
        this(ingredient, count, 1);
    }

    @Override
    protected ItemStack[] initValues()
    {
        return Arrays.stream(ingredient.getItems()).map(o -> o.copyWithCount(size)).toArray(ItemStack[]::new);
    }
}