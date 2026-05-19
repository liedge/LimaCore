package liedge.limacore.recipe.input;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.EmptyFieldMapCodec;
import liedge.limacore.data.LimaCoreCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.display.ForFluidStacks;

import java.util.List;

public record RecipeFluidInput(FluidIngredient ingredient, int count, float consumeChance) implements RecipeStackInput<FluidIngredient, FluidStack>
{
    public static final Codec<RecipeFluidInput> CODEC = RecipeStackInput.codec(
            FluidIngredient.CODEC,
            ExtraCodecs.POSITIVE_INT.fieldOf("amount"),
            RecipeFluidInput::new);
    public static final Codec<RecipeFluidInput> CONSTANT_CODEC = RecipeStackInput.constantCodec(CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeFluidInput> STREAM_CODEC = RecipeStackInput.streamCodec(FluidIngredient.STREAM_CODEC, RecipeFluidInput::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, List<RecipeFluidInput>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

    public static final String MAP_CODEC_KEY = "fluid_inputs";
    public static final MapCodec<List<RecipeFluidInput>> EMPTY_LIST_CODEC = EmptyFieldMapCodec.emptyListField(MAP_CODEC_KEY);

    public static MapCodec<List<RecipeFluidInput>> listMapCodec(int minInclusive, int maxInclusive)
    {
        return LimaCoreCodecs.autoOptionalListField(CODEC, MAP_CODEC_KEY, minInclusive, maxInclusive);
    }

    @Override
    public ForFluidStacks<FluidStack> displayResolver()
    {
        return stack -> stack.copyWithAmount(count);
    }
}