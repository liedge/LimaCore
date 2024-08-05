package liedge.limacore.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class LimaCollectionsUtil
{
    private LimaCollectionsUtil() {}

    //#region Stream collectors
    private static <E, C extends Collection<E>> C combineCollections(C collection1, C collection2)
    {
        collection1.addAll(collection2);
        return collection1;
    }

    private static <K, V, M extends Map<K, V>> M combineMaps(M map1, M map2)
    {
        for (Map.Entry<K, V> entry : map2.entrySet())
        {
            putNoDuplicates(map1, entry.getKey(), entry.getValue());
        }

        return map1;
    }

    public static <T> Collector<T, ?, ObjectList<T>> toObjectArrayList()
    {
        return Collectors.toCollection(ObjectArrayList::new);
    }

    public static <T> Collector<T, ObjectList<T>, ObjectList<T>> toUnmodifiableObjectArrayList()
    {
        return Collector.of(ObjectArrayList::new, ObjectList::add, LimaCollectionsUtil::combineCollections, ObjectLists::unmodifiable);
    }

    public static <T> Collector<T, ?, ObjectSet<T>> toObjectSet()
    {
        return Collectors.toCollection(ObjectOpenHashSet::new);
    }

    public static <T> Collector<T, ObjectSet<T>, ObjectSet<T>> toUnmodifiableObjectSet()
    {
        return Collector.of(ObjectOpenHashSet::new, ObjectSet::add, LimaCollectionsUtil::combineCollections, ObjectSets::unmodifiable);
    }

    public static <T> Collector<T, ?, NonNullList<T>> toNonNullList()
    {
        return Collectors.toCollection(NonNullList::create);
    }

    public static <E extends Enum<E>, V> Collector<E, ?, Map<E, V>> toEnumMap(Class<E> enumClass, Function<? super E, ? extends V> valueMapper)
    {
        return Collector.of(() -> new EnumMap<>(enumClass), (map, e) -> putNoDuplicates(map, e, valueMapper.apply(e)), LimaCollectionsUtil::combineMaps, Collector.Characteristics.IDENTITY_FINISH);
    }

    public static <E extends Enum<E>, V> Collector<E, EnumMap<E, V>, Map<E, V>> toUnmodifiableEnumMap(Class<E> enumClass, Function<? super E, ? extends V> valueMapper)
    {
        return Collector.of(() -> new EnumMap<>(enumClass), (map, e) -> putNoDuplicates(map, e, valueMapper.apply(e)), LimaCollectionsUtil::combineMaps, ImmutableMap::copyOf);
    }

    public static <T, K, U> Collector<T, ?, Object2ObjectMap<K, U>> toObject2ObjectMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper)
    {
        return Collector.of(Object2ObjectOpenHashMap::new, (map, t) -> putNoDuplicates(map, keyMapper.apply(t), valueMapper.apply(t)), LimaCollectionsUtil::combineMaps, Collector.Characteristics.IDENTITY_FINISH);
    }

    public static <T, K, U> Collector<T, Object2ObjectMap<K, U>, Object2ObjectMap<K, U>> toUnmodifiableObject2ObjectMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper)
    {
        return Collector.of(Object2ObjectOpenHashMap::new, (map, t) -> putNoDuplicates(map, keyMapper.apply(t), valueMapper.apply(t)), LimaCollectionsUtil::combineMaps, Object2ObjectMaps::unmodifiable);
    }

    public static Collector<Tag, ?, ListTag> toTagList()
    {
        return Collectors.toCollection(ListTag::new);
    }
    //#endregion

    public static Stream<CompoundTag> streamCompoundList(ListTag tag)
    {
        if (tag.isEmpty()) return Stream.empty();
        Preconditions.checkArgument(tag.getElementType() == Tag.TAG_COMPOUND, "NBT tag list is not a compound tag list");
        return tag.stream().map(t -> (CompoundTag) t);
    }

    public static IntList toIntList(IntStream stream)
    {
        return toIntCollection(stream, IntArrayList::new);
    }

    public static IntSet toIntSet(IntStream stream)
    {
        return toIntCollection(stream, IntOpenHashSet::new);
    }

    public static <T> NonNullList<T> nonNullListOf(T value)
    {
        NonNullList<T> list = NonNullList.createWithCapacity(1);
        list.add(value);
        return list;
    }

    public static <E extends Enum<E>> E[] checkedEnumConstants(Class<E> enumClass)
    {
        return Objects.requireNonNull(enumClass.getEnumConstants(), "Enum constants not found");
    }

    public static <E extends Enum<E>> Stream<E> enumStream(Class<E> enumClass)
    {
        return Arrays.stream(checkedEnumConstants(enumClass));
    }

    public static <E extends Enum<E>, U> Map<E, U> fillAndCreateEnumMap(Class<E> enumClass, Function<E, ? extends U> mapper)
    {
        return enumStream(enumClass).collect(toEnumMap(enumClass, mapper));
    }

    public static <E extends Enum<E>, U> Map<E, U> fillAndCreateImmutableEnumMap(Class<E> enumClass, Function<E, ? extends U> mapper)
    {
        return enumStream(enumClass).collect(toUnmodifiableEnumMap(enumClass, mapper));
    }

    public static <K, V> void putNoDuplicates(Map<K, V> map, K key, V value)
    {
        V added = map.putIfAbsent(key, value);
        if (added != null)
        {
            throw new IllegalArgumentException("Duplicate map key '" + key + ".");
        }
    }

    public static int splitCollectionToSegments(Collection<?> collection, int segmentSize)
    {
        int size = collection.size();
        return size / segmentSize + (size % segmentSize == 0 ? 0 : 1);
    }

    // Helpers
    private static <T extends IntCollection> T toIntCollection(IntStream stream, Supplier<T> supplier)
    {
        return stream.collect(supplier, IntCollection::add, IntCollection::addAll);
    }
}