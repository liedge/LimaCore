package liedge.limacore.recipe.input;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.EmptyFieldMapCodec;
import liedge.limacore.data.LimaCoreCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

public record RecipeItemInput(Ingredient ingredient, int count, float consumeChance) implements RecipeStackInput<Ingredient, ItemStack>
{
    public static final Codec<RecipeItemInput> CODEC = RecipeStackInput.codec(
            Ingredient.CODEC,
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1),
            RecipeItemInput::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeItemInput> STREAM_CODEC = RecipeStackInput.streamCodec(Ingredient.CONTENTS_STREAM_CODEC, RecipeItemInput::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, List<RecipeItemInput>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

    public static final String MAP_CODEC_KEY = "item_inputs";
    public static final MapCodec<List<RecipeItemInput>> EMPTY_LIST_CODEC = EmptyFieldMapCodec.emptyListField(MAP_CODEC_KEY);

    public static MapCodec<List<RecipeItemInput>> listCodec(int minInclusive, int maxInclusive)
    {
        return LimaCoreCodecs.autoOptionalListField(CODEC, MAP_CODEC_KEY, minInclusive, maxInclusive);
    }
}