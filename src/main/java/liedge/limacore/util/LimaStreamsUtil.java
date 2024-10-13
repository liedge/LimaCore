package liedge.limacore.util;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static liedge.limacore.util.LimaCollectionsUtil.checkedEnumConstants;
import static liedge.limacore.util.LimaCollectionsUtil.putNoDuplicates;

public final class LimaStreamsUtil
{
    private LimaStreamsUtil() { }

    // Collection helpers
    public static <T, C extends Collection<T>> BinaryOperator<C> collectionMerger()
    {
        return LimaCollectionsUtil::mergeIntoFirstCollection;
    }

    public static <T, K, V, M extends Map<K, V>> BiConsumer<M, T> mapAccumulator(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper)
    {
        return (map, t) -> putNoDuplicates(map, keyMapper.apply(t), valueMapper.apply(t));
    }

    public static <K, V, M extends Map<K, V>> BiConsumer<M, K> idKeyMapAccumulator(Function<? super K, ? extends V> valueMapper)
    {
        return (map, key) -> putNoDuplicates(map, key, valueMapper.apply(key));
    }

    public static <K, V, M extends Map<K, V>> BinaryOperator<M> mapMerger()
    {
        return LimaCollectionsUtil::mergeIntoFirstMap;
    }

    //#region Stream collectors
    public static <T> Collector<T, ?, ObjectList<T>> toObjectList()
    {
        return Collectors.toCollection(ObjectArrayList::new);
    }

    public static <T> Collector<T, ObjectList<T>, ObjectList<T>> toUnmodifiableObjectList()
    {
        return Collector.of(ObjectArrayList::new, ObjectList::add, collectionMerger(), ObjectLists::unmodifiable);
    }

    public static <T> Collector<T, ?, ObjectSet<T>> toObjectSet()
    {
        return Collectors.toCollection(ObjectOpenHashSet::new);
    }

    public static <T> Collector<T, ObjectSet<T>, ObjectSet<T>> toUnmodifiableObjectSet()
    {
        return Collector.of(ObjectOpenHashSet::new, ObjectSet::add, collectionMerger(), ObjectSets::unmodifiable);
    }

    public static <T> Collector<T, ?, NonNullList<T>> toNonNullList()
    {
        return Collectors.toCollection(NonNullList::create);
    }

    public static <T, K, V, M extends Map<K, V>> Collector<T, ?, M> toMap(Supplier<M> mapSupplier, Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper)
    {
        return Collector.of(mapSupplier, mapAccumulator(keyMapper, valueMapper), mapMerger(), Collector.Characteristics.IDENTITY_FINISH);
    }

    public static <K, V, M extends Map<K, V>> Collector<K, ?, M> toIdKeyMap(Supplier<M> mapSupplier, Function<? super K, ? extends V> valueMapper)
    {
        return Collector.of(mapSupplier, idKeyMapAccumulator(valueMapper), mapMerger(), Collector.Characteristics.IDENTITY_FINISH);
    }

    public static <T, K, V, A extends Map<K, V>, R extends Map<K, V>> Collector<T, A, R> toMap(Supplier<A> accumulatorMapSupplier, Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper, Function<A, R> finisher)
    {
        return Collector.of(accumulatorMapSupplier, mapAccumulator(keyMapper, valueMapper), mapMerger(), finisher);
    }

    public static <K, V, A extends Map<K, V>, R extends Map<K, V>> Collector<K, A, R> toIdKeyMap(Supplier<A> accumulatorMapSupplier, Function<? super K, ? extends V> valueMapper, Function<A, R> finisher)
    {
        return Collector.of(accumulatorMapSupplier, idKeyMapAccumulator(valueMapper), mapMerger(), finisher);
    }

    public static <E extends Enum<E>, V> Collector<E, ?, Map<E, V>> toEnumMap(Class<E> enumClass, Function<? super E, ? extends V> valueMapper)
    {
        return toIdKeyMap(() -> new EnumMap<>(enumClass), valueMapper);
    }

    public static <E extends Enum<E>, V> Collector<E, EnumMap<E, V>, Map<E, V>> toUnmodifiableEnumMap(Class<E> enumClass, Function<? super E, ? extends V> valueMapper)
    {
        return toIdKeyMap(() -> new EnumMap<>(enumClass), valueMapper, ImmutableMap::copyOf);
    }

    public static <T, K, U> Collector<T, ?, Object2ObjectMap<K, U>> toObject2ObjectMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper)
    {
        return toMap(Object2ObjectOpenHashMap::new, keyMapper, valueMapper);
    }

    public static <T, K, U> Collector<T, ?, Object2ObjectMap<K, U>> toUnmodifiableObject2ObjectMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper)
    {
        return toMap(Object2ObjectOpenHashMap::new, keyMapper, valueMapper, Object2ObjectMaps::unmodifiable);
    }

    public static Collector<Tag, ?, ListTag> toTagList()
    {
        return Collectors.toCollection(ListTag::new);
    }
    //#endregion

    public static <E extends Enum<E>> Stream<E> enumStream(Class<E> enumClass)
    {
        return Arrays.stream(checkedEnumConstants(enumClass));
    }

    public static <T> Stream<T> buildStream(Consumer<Consumer<T>> elementSupplier)
    {
        Stream.Builder<T> builder = Stream.builder();
        elementSupplier.accept(builder);
        return builder.build();
    }
}