package liedge.limacore.recipe.ingredient;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

import java.util.Objects;
import java.util.function.Predicate;

public abstract sealed class LimaSizedIngredient<I extends Predicate<B>, B> implements Predicate<B> permits LimaSizedItemIngredient, LimaSizedFluidIngredient
{
    static <I extends Predicate<?>, A extends LimaSizedIngredient<I, ?>> Codec<A> codec(Codec<I> ingredientCodec, String sizeFieldName, Function3<I, Integer, Float, A> constructor)
    {
        return RecordCodecBuilder.create(instance -> instance.group(
                ingredientCodec.fieldOf("ingredient").forGetter(A::getIngredient),
                NeoForgeExtraCodecs.optionalFieldAlwaysWrite(ExtraCodecs.POSITIVE_INT, sizeFieldName, 1).forGetter(A::getSize),
                LimaCoreCodecs.floatOpenEndRange(0f, 1f).optionalFieldOf("consume_chance", 1f).forGetter(A::getConsumeChance))
                .apply(instance, constructor));
    }

    static <I extends Predicate<?>, A extends LimaSizedIngredient<I, ?>> StreamCodec<RegistryFriendlyByteBuf, A> streamCodec(StreamCodec<RegistryFriendlyByteBuf, I> ingredientCodec, Function3<I, Integer, Float, A> constructor)
    {
        return StreamCodec.composite(
                ingredientCodec,
                A::getIngredient,
                ByteBufCodecs.VAR_INT,
                A::getSize,
                ByteBufCodecs.FLOAT,
                A::getConsumeChance,
                constructor);
    }

    protected final I ingredient;
    protected final int size;
    private final float consumeChance;

    LimaSizedIngredient(I ingredient, int size, float consumeChance)
    {
        this.ingredient = ingredient;
        this.size = size;
        this.consumeChance = consumeChance;
    }

    public I getIngredient()
    {
        return ingredient;
    }

    public int getSize()
    {
        return size;
    }

    public float getConsumeChance()
    {
        return consumeChance;
    }

    public boolean isDeterministic()
    {
        return consumeChance < 1;
    }

    @Override
    public final boolean test(B base)
    {
        return ingredient.test(base);
    }

    @Override
    public final boolean equals(Object obj)
    {
        if (this == obj) return true;
        else if (!(obj instanceof LimaSizedIngredient<?,?> other)) return false;
        else return this.size == other.size && this.ingredient.equals(other.ingredient) && this.consumeChance == other.consumeChance;
    }

    @Override
    public final int hashCode()
    {
        return Objects.hash(ingredient, size, consumeChance);
    }

    @Override
    public final String toString()
    {
        return size + "x " + ingredient;
    }
}