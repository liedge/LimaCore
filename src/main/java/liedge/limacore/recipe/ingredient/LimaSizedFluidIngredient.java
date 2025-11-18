package liedge.limacore.recipe.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.EmptyFieldMapCodec;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.network.LimaStreamCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.Arrays;
import java.util.List;

public final class LimaSizedFluidIngredient extends LimaSizedIngredient<FluidIngredient, FluidStack>
{
    public static final String MAP_CODEC_KEY = "fluid_ingredients";
    public static final Codec<LimaSizedFluidIngredient> CODEC = codec(FluidIngredient.MAP_CODEC_NONEMPTY, "amount", LimaSizedFluidIngredient::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, LimaSizedFluidIngredient> STREAM_CODEC = streamCodec(FluidIngredient.STREAM_CODEC, LimaSizedFluidIngredient::new);
    public static final MapCodec<List<LimaSizedFluidIngredient>> LIST_UNIT_MAP_CODEC = EmptyFieldMapCodec.emptyListField(MAP_CODEC_KEY);

    public static MapCodec<List<LimaSizedFluidIngredient>> listMapCodec(int minInclusive, int maxExclusive)
    {
        return LimaCoreCodecs.autoOptionalListField(CODEC, MAP_CODEC_KEY, minInclusive, maxExclusive);
    }

    public static StreamCodec<RegistryFriendlyByteBuf, List<LimaSizedFluidIngredient>> listStreamCodec(int minInclusive, int maxInclusive)
    {
        return STREAM_CODEC.apply(LimaStreamCodecs.asClampedList(minInclusive, maxInclusive));
    }

    public LimaSizedFluidIngredient(FluidIngredient ingredient, int amount, float consumeChance)
    {
        super(ingredient, amount, consumeChance);
    }

    public LimaSizedFluidIngredient(FluidIngredient ingredient, int amount)
    {
        this(ingredient, amount, 1f);
    }

    @Override
    protected FluidStack[] initValues()
    {
        return Arrays.stream(ingredient.getStacks()).map(o -> o.copyWithAmount(size)).toArray(FluidStack[]::new);
    }
}