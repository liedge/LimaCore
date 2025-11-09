package liedge.limacore.recipe.ingredient;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import net.minecraft.util.RandomSource;

import java.util.function.BiFunction;

public interface DeterministicIngredient<T>
{
    static <I, A extends DeterministicIngredient<I>> Products.P2<RecordCodecBuilder.Mu<A>, I, Float> codecPrefix(RecordCodecBuilder.Instance<A> instance, Codec<I> childCodec)
    {
        return instance.group(
                childCodec.fieldOf("child").forGetter(A::child),
                LimaCoreCodecs.floatOpenEndRange(0f, 1f).fieldOf("chance").forGetter(A::consumeChance));
    }

    static <I, A extends DeterministicIngredient<I>> MapCodec<A> codec(Codec<I> childCodec, BiFunction<I, Float, A> constructor)
    {
        return RecordCodecBuilder.mapCodec(instance -> codecPrefix(instance, childCodec).apply(instance, constructor));
    }

    T child();

    float consumeChance();

    default boolean shouldConsume(RandomSource random)
    {
        float chance = consumeChance();
        return chance != 0 && random.nextFloat() < chance;
    }
}