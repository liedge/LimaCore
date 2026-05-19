package liedge.limacore.recipe.result;

import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import liedge.limacore.data.LimaCoreCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.function.UnaryOperator;

public interface RecipeResult<T, R extends Resource> extends Comparable<RecipeResult<T, R>>, DataComponentGetter
{
    static <T, R extends Resource, A extends RecipeResult<T, R>> Codec<A> codec(Codec<Holder<T>> typeCodec, MapCodec<ResultCount> countCodec, Function4<Holder<T>, ResultCount, DataComponentPatch, Boolean, A> factory)
    {
        return RecordCodecBuilder.create(i -> i.group(
                typeCodec.fieldOf("id").forGetter(RecipeResult::typeHolder),
                countCodec.forGetter(RecipeResult::count),
                DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(RecipeResult::components),
                Codec.BOOL.optionalFieldOf("required", true).forGetter(RecipeResult::required))
                .apply(i, factory));
    }

    static <A extends RecipeResult<?, ?>> Codec<A> constantCodec(Codec<A> baseCodec)
    {
        return baseCodec.validate(value ->
        {
            if (value.count().isConstant())
                return DataResult.success(value);
            else
                return DataResult.error(() -> "Recipe result must have a fixed count and not be random.");
        });
    }

    static <T, R extends Resource, A extends RecipeResult<T, R>> StreamCodec<RegistryFriendlyByteBuf, A> streamCodec(StreamCodec<RegistryFriendlyByteBuf, Holder<T>> typeCodec, Function4<Holder<T>, ResultCount, DataComponentPatch, Boolean, A> factory)
    {
        return StreamCodec.composite(
                typeCodec, RecipeResult::typeHolder,
                ResultCount.STREAM_CODEC, RecipeResult::count,
                DataComponentPatch.STREAM_CODEC, RecipeResult::components,
                ByteBufCodecs.BOOL, RecipeResult::required,
                factory);
    }

    static <T extends RecipeResult<?, ?>> MapCodec<List<T>> listCodec(Codec<T> elementCodec, String key, int minInclusive, int maxInclusive)
    {
        final UnaryOperator<List<T>> sorter = list ->
        {
            ObjectList<T> sorted = new ObjectArrayList<>(list);
            sorted.sort(Comparator.naturalOrder());
            return ObjectLists.unmodifiable(sorted);
        };

        return LimaCoreCodecs.autoOptionalListField(elementCodec, key, minInclusive, maxInclusive).xmap(sorter, sorter);
    }

    Holder<T> typeHolder();

    ResultCount count();

    DataComponentPatch components();

    boolean required();

    R getResource();

    default ResourceStack<R> createResource(RandomSource random)
    {
        return new ResourceStack<>(getResource(), count().rollCount(random));
    }

    @Override
    default <DT> @Nullable DT get(DataComponentType<? extends DT> type)
    {
        return components().get(typeHolder().components(), type);
    }

    @Override
    default int compareTo(RecipeResult<T, R> o)
    {
        return Boolean.compare(o.required(), this.required());
    }
}