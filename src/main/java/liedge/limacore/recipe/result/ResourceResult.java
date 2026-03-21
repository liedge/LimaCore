package liedge.limacore.recipe.result;

import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import liedge.limacore.data.LimaCoreCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;

import java.util.Comparator;
import java.util.List;
import java.util.function.UnaryOperator;

public abstract class ResourceResult<T extends Resource> implements Comparable<ResourceResult<T>>
{
    static <R extends Resource, T extends ResourceResult<R>> Codec<T> codec(MapCodec<R> resourceMapCodec, int countLimit, Factory<R, T> factory)
    {
        return RecordCodecBuilder.create(instance -> instance.group(
                resourceMapCodec.forGetter(ResourceResult::getResource),
                ResultCount.codec(countLimit).fieldOf("count").forGetter(ResourceResult::getCount),
                Codec.floatRange(0f, 1f).optionalFieldOf("chance", 1f).forGetter(ResourceResult::getChance),
                ResultPriority.CODEC.optionalFieldOf("priority", ResultPriority.PRIMARY).forGetter(ResourceResult::getPriority))
                .apply(instance, factory));
    }

    static <R extends Resource, T extends ResourceResult<R>> StreamCodec<RegistryFriendlyByteBuf, T> streamCodec(StreamCodec<RegistryFriendlyByteBuf, R> resourceCodec, Factory<R, T> factory)
    {
        return StreamCodec.composite(
                resourceCodec, ResourceResult::getResource,
                ResultCount.STREAM_CODEC, ResourceResult::getCount,
                ByteBufCodecs.FLOAT, ResourceResult::getChance,
                ResultPriority.STREAM_CODEC, ResourceResult::getPriority,
                factory);
    }

    static <R extends Resource, T extends ResourceResult<R>> MapCodec<List<T>> listMapCodec(Codec<T> codec, String key, int min, int max)
    {
        UnaryOperator<List<T>> sorter = list ->
        {
            if (list.size() < 2) return list;

            ObjectList<T> sorted = new ObjectArrayList<>(list);
            sorted.sort(Comparator.naturalOrder());
            return ObjectLists.unmodifiable(sorted);
        };

        return LimaCoreCodecs.autoOptionalListField(codec, key, min, max).xmap(sorter, sorter);
    }

    protected final T resource;
    protected final ResultCount count;
    protected final float chance;
    protected final ResultPriority priority;

    ResourceResult(T resource, ResultCount count, float chance, ResultPriority priority)
    {
        this.resource = resource;
        this.count = count;
        this.chance = chance;
        this.priority = priority;
    }

    public T getResource()
    {
        return resource;
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

    public ResourceStack<T> generateResult(RandomSource random)
    {
        int amount;

        if (chance == 1 || random.nextFloat() < chance)
        {
            amount = count.applyAsInt(random);
        }
        else
        {
            amount = 0;
        }

        return new ResourceStack<>(resource, amount);
    }

    @Override
    public int compareTo(ResourceResult<T> o)
    {
        return this.priority.compareTo(o.priority);
    }

    @FunctionalInterface
    interface Factory<R extends Resource, T extends ResourceResult<R>> extends Function4<R, ResultCount, Float, ResultPriority, T>
    {}
}