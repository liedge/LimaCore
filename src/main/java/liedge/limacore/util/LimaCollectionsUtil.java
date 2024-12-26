package liedge.limacore.util;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static liedge.limacore.util.LimaStreamsUtil.*;

public final class LimaCollectionsUtil
{
    private LimaCollectionsUtil() {}

    //#region Collection modification helpers
    public static <E, C extends Collection<E>> C mergeIntoFirstCollection(C collection1, C collection2)
    {
        collection1.addAll(collection2);
        return collection1;
    }

    public static <K, V, M extends Map<K, V>> M mergeIntoFirstMap(M map1, M map2)
    {
        for (Map.Entry<K, V> entry : map2.entrySet())
        {
            putNoDuplicates(map1, entry.getKey(), entry.getValue());
        }

        return map1;
    }
    //#endregion

    // Indexed collection helpers
    public static <T> @Nullable T getFrom(T[] array, int index)
    {
        return (index >= 0 && index < array.length) ? array[index] : null;
    }

    public static <T> @Nullable T getFrom(List<T> list, int index)
    {
        return (index >= 0 && index < list.size()) ? list.get(index) : null;
    }

    public static <T> int addAndGetIndex(List<T> list, T element)
    {
        int index = list.size();
        list.add(element);
        return index;
    }

    // Misc helpers
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

    public static <E extends Enum<E>> E[] checkedEnumConstants(Class<E> enumClass)
    {
        return Objects.requireNonNull(enumClass.getEnumConstants(), "Enum constants not found");
    }

    public static <E extends Enum<E>> E getEnumByOrdinal(Class<E> enumClass, int ordinal)
    {
        E[] values = checkedEnumConstants(enumClass);
        Preconditions.checkElementIndex(ordinal, values.length, "Enum ordinal");
        return values[ordinal];
    }

    public static <E extends Enum<E>, U> Map<E, U> fillAndCreateEnumMap(Class<E> enumClass, Function<E, ? extends U> mapper)
    {
        return enumStream(enumClass).collect(toEnumMap(enumClass, mapper));
    }

    public static <E extends Enum<E>, U> Map<E, U> fillAndCreateImmutableEnumMap(Class<E> enumClass, Function<E, ? extends U> mapper)
    {
        return enumStream(enumClass).collect(toUnmodifiableEnumMap(enumClass, mapper));
    }

    public static <K, V, X extends Throwable> void putNoDuplicates(Map<K, V> map, K key, V value, Supplier<X> exceptionSupplier) throws X
    {
        V added = map.putIfAbsent(key, value);
        if (added != null) throw exceptionSupplier.get();
    }

    public static <K, V> void putNoDuplicates(Map<K, V> map, K key, V value)
    {
        putNoDuplicates(map, key, value, () -> new IllegalArgumentException("Duplicate map key '" + key + "'."));
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