package liedge.limacore.recipe.input;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;

import java.util.function.Predicate;

public interface RecipeStackInput<I extends Predicate<S>, S> extends Predicate<S>
{
    static <I extends Predicate<?>, A extends RecipeStackInput<I, ?>> Codec<A> codec(Codec<I> ingredientCodec, MapCodec<Integer> countMapCodec, Function3<I, Integer, Float, A> factory)
    {
        return RecordCodecBuilder.create(i -> i.group(
                ingredientCodec.fieldOf("ingredient").forGetter(A::ingredient),
                countMapCodec.forGetter(RecipeStackInput::count),
                Codec.floatRange(0f, 1f).optionalFieldOf("consume_chance", 1f).forGetter(RecipeStackInput::consumeChance))
                .apply(i, factory));
    }

    static <A extends RecipeStackInput<?, ?>> Codec<A> constantCodec(Codec<A> baseCodec)
    {
        return baseCodec.validate(value ->
        {
            if (value.isConstant())
                return DataResult.success(value);
            else
                return DataResult.error(() -> "Recipe input cannot be random.");
        });
    }

    static <I extends Predicate<?>, A extends RecipeStackInput<I, ?>> StreamCodec<RegistryFriendlyByteBuf, A> streamCodec(StreamCodec<RegistryFriendlyByteBuf, I> ingredientCodec, Function3<I, Integer, Float, A> factory)
    {
        return StreamCodec.composite(
                ingredientCodec, A::ingredient,
                ByteBufCodecs.VAR_INT, RecipeStackInput::count,
                ByteBufCodecs.FLOAT, RecipeStackInput::consumeChance,
                factory);
    }

    I ingredient();

    int count();

    float consumeChance();

    DisplayContentsFactory<S> displayResolver();

    default boolean isConstant()
    {
        return !isRandom();
    }

    default boolean isRandom()
    {
        return consumeChance() < 1f;
    }

    @Override
    default boolean test(S stackBase)
    {
        return ingredient().test(stackBase);
    }
}