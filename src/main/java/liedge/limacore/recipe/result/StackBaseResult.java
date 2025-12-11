package liedge.limacore.recipe.result;

import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import liedge.limacore.data.LimaCoreCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;

import java.util.Comparator;
import java.util.List;
import java.util.function.UnaryOperator;

public abstract class StackBaseResult<B, T>
{
    public static final Comparator<StackBaseResult<?, ?>> BY_PRIORITY = Comparator.comparing(StackBaseResult::getPriority);

    static <B, T, R extends StackBaseResult<B, T>> Codec<R> codec(Codec<Holder<B>> baseCodec, String countFieldName, int countLimit, ResultConstructor<B, T, R> constructor)
    {
        return RecordCodecBuilder.create(instance -> instance.group(
                baseCodec.fieldOf("id").forGetter(StackBaseResult::getBase),
                DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(o -> o.components),
                ResultCount.codec(countLimit).fieldOf(countFieldName).forGetter(StackBaseResult::getCount),
                Codec.floatRange(0f, 1f).optionalFieldOf("chance", 1f).forGetter(StackBaseResult::getChance),
                ResultPriority.CODEC.optionalFieldOf("priority", ResultPriority.PRIMARY).forGetter(StackBaseResult::getPriority))
                .apply(instance, constructor));
    }

    static <B, T, R extends StackBaseResult<B, T>> StreamCodec<RegistryFriendlyByteBuf, R> streamCodec(StreamCodec<RegistryFriendlyByteBuf, Holder<B>> baseCodec, ResultConstructor<B, T, R> constructor)
    {
        return StreamCodec.composite(
                baseCodec, StackBaseResult::getBase,
                DataComponentPatch.STREAM_CODEC, o -> o.components,
                ResultCount.STREAM_CODEC, StackBaseResult::getCount,
                ByteBufCodecs.FLOAT, StackBaseResult::getChance,
                ResultPriority.STREAM_CODEC, StackBaseResult::getPriority,
                constructor);
    }

    static <B, T, R extends StackBaseResult<B, T>> MapCodec<List<R>> createListMapCodec(Codec<R> codec, String key, int min, int max)
    {
        UnaryOperator<List<R>> sorter = list ->
        {
            if (list.size() < 2) return list;

            ObjectList<R> sorted = new ObjectArrayList<>(list);
            sorted.sort(BY_PRIORITY);
            return ObjectLists.unmodifiable(sorted);
        };

        return LimaCoreCodecs.autoOptionalListField(codec, key, min, max).xmap(sorter, sorter);
    }

    protected final Holder<B> base;
    protected final DataComponentPatch components;
    protected final ResultCount count;
    protected final float chance;
    protected final ResultPriority priority;

    private T maxStack;
    private T displayStack;

    StackBaseResult(Holder<B> base, DataComponentPatch components, ResultCount count, float chance, ResultPriority priority)
    {
        this.base = base;
        this.components = components;
        this.count = count;
        this.chance = chance;
        this.priority = priority;
    }

    protected abstract T createStack(int stackSize);

    protected abstract T getEmptyStack();

    public Holder<B> getBase()
    {
        return base;
    }

    public ResultCount getCount()
    {
        return count;
    }

    public float getChance()
    {
        return chance;
    }

    public ResultPriority getPriority()
    {
        return priority;
    }

    public T getMaxStack()
    {
        if (maxStack == null) maxStack = createStack(count.max());
        return maxStack;
    }

    public T getDisplayStack()
    {
        if (displayStack == null)
        {
            int displayCount = count.isConstant() ? count.max() : 1;
            displayStack = createStack(displayCount);
        }

        return displayStack;
    }

    public T generateResult(RandomSource random)
    {
        if (chance == 1 || random.nextFloat() < chance)
        {
            return createStack(count.applyAsInt(random));
        }
        else
        {
            return getEmptyStack();
        }
    }

    @FunctionalInterface
    interface ResultConstructor<B, T, R extends StackBaseResult<B, T>> extends Function5<Holder<B>, DataComponentPatch, ResultCount, Float, ResultPriority, R> { }
}