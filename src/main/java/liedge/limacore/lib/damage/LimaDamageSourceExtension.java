package liedge.limacore.lib.damage;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Extension of {@link MutableDataComponentHolder} with all methods made default
 * to meet the interface injection mixin requirements.
 * @apiNote For use by {@link net.minecraft.world.damagesource.DamageSource} mixin only. Do not implement.
 */
public interface LimaDamageSourceExtension extends MutableDataComponentHolder
{
    // Lima extensions
    default PatchedDataComponentMap getModifiableComponents()
    {
        throw new IllegalStateException("Interface LimaDamageSourceExtension implementation must override getModifiableComponents.");
    }

    default <T> List<T> getList(DataComponentType<? extends List<T>> componentType)
    {
        return getOrDefault(componentType, List.of());
    }

    default <T> Set<T> getSet(DataComponentType<? extends Set<T>> componentType)
    {
        return getOrDefault(componentType, Set.of());
    }

    private <T, C extends Collection<T>> C mergeElement(DataComponentType<C> componentType, T element, Function<? super C, ? extends C> factory, C emptyCollection)
    {
        C oldCollection = getOrDefault(componentType, emptyCollection);
        C newCollection = factory.apply(oldCollection);
        newCollection.add(element);
        set(componentType, newCollection);
        return oldCollection;
    }

    private <T, C extends Collection<T>> C mergeCollection(DataComponentType<C> componentType, Collection<? extends T> toAdd, Function<? super C, ? extends C> factory, C emptyCollection)
    {
        C oldCollection = getOrDefault(componentType, emptyCollection);
        if (!toAdd.isEmpty())
        {
            C newCollection = factory.apply(oldCollection);
            newCollection.addAll(toAdd);
            set(componentType, newCollection);
        }
        return oldCollection;
    }

    default <T> List<T> mergeList(DataComponentType<List<T>> componentType, Collection<? extends T> toAdd)
    {
        return mergeCollection(componentType, toAdd, ObjectArrayList::new, List.of());
    }

    default <T> List<T> mergeListElement(DataComponentType<List<T>> componentType, T element)
    {
        return mergeElement(componentType, element, ObjectArrayList::new, List.of());
    }

    default <T> Set<T> mergeSet(DataComponentType<Set<T>> componentType, Collection<? extends T> toAdd)
    {
        return mergeCollection(componentType, toAdd, ObjectOpenHashSet::new, Set.of());
    }

    default <T> Set<T> mergeSetElement(DataComponentType<Set<T>> componentType, T element)
    {
        return mergeElement(componentType, element, ObjectOpenHashSet::new, Set.of());
    }

    // Standard extensions
    @Override
    default DataComponentMap getComponents()
    {
        return DataComponentMap.EMPTY;
    }

    @Override
    default <T> @Nullable T set(DataComponentType<? super T> componentType, @Nullable T value)
    {
        return getModifiableComponents().set(componentType, value);
    }

    @Override
    default <T> @Nullable T remove(DataComponentType<? extends T> componentType)
    {
        return getModifiableComponents().remove(componentType);
    }

    @Override
    default void applyComponents(DataComponentPatch patch)
    {
        getModifiableComponents().applyPatch(patch);
    }

    @Override
    default void applyComponents(DataComponentMap components)
    {
        getModifiableComponents().setAll(components);
    }
}