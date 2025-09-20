package liedge.limacore.util;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static liedge.limacore.util.LimaStreamsUtil.*;

public final class LimaCollectionsUtil
{
    private LimaCollectionsUtil() {}

    //#region Collection modification helpers
    /**
     * Appends {@code second} into {@code first} with {@link Collection#addAll(Collection)}.
     * For use when combining collections and returning the destination collection in one line is needed.
     * @param first The 'destination' collection
     * @param second The 'source' collection
     * @return The first collection
     * @param <E> The type of the collection element
     * @param <C> The type of the first collection
     */
    public static <E, C extends Collection<E>> C mergeCollections(C first, Collection<E> second)
    {
        first.addAll(second);
        return first;
    }

    /**
     * Puts all {@code second} entries into {@code first} using {@link LimaCollectionsUtil#putNoDuplicates(Map, K, V)}
     * @param first The 'destination' of the map entries
     * @param second The 'source' of the map entries
     * @return The first map
     * @param <K> The type of the map keys
     * @param <V> The type of the map values
     * @param <M> The type of the first (destination) map
     */
    public static <K, V, M extends Map<K, V>> M mergeMapsNoDuplicates(M first, Map<K, V> second)
    {
        for (Map.Entry<K, V> entry : second.entrySet())
        {
            putNoDuplicates(first, entry.getKey(), entry.getValue());
        }

        return first;
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
    public static int[] shuffleIntArray(int[] array, RandomSource random)
    {
        for (int a = array.length - 1; a > 0; a--)
        {
            int b = random.nextInt(a + 1);
            IntArrays.swap(array, a, b);
        }

        return array;
    }

    public static int[] shuffleIndices(List<?> list, RandomSource random)
    {
        int[] arr = new int[list.size()];
        Arrays.setAll(arr, IntUnaryOperator.identity());

        return shuffleIntArray(arr, random);
    }

    public static <T> List<T> shuffledList(List<T> source, RandomSource random)
    {
        List<T> result = new ObjectArrayList<>();

        int[] si = shuffleIndices(source, random);

        for (int i : si)
        {
            result.add(source.get(i));
        }

        return result;
    }

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

    /**
     * Attempts to insert an entry into a map with {@link Map#putIfAbsent(K, V)}. If the key is already in the map and
     * associated with a non-null value, an {@link IllegalArgumentException} will be thrown.
     * @param map The map object
     * @param key The key of the map entry
     * @param value The value of the map entry
     * @param <K> The type of the map keys
     * @param <V> The type of the map values
     * @throws IllegalArgumentException If a key previously had a non-null value assigned to it.
     */
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