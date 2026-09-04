package liedge.limacore.data;

import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public abstract class MapLikeData<K, V>
{
    protected final Map<K, V> map;
    private final int hashCode;

    protected MapLikeData(Map<K, V> map)
    {
        this.map = map;
        this.hashCode = map.hashCode();
    }

    public @Nullable V get(K key)
    {
        return map.get(key);
    }

    public V getOrDefault(K key, V defaultValue)
    {
        return map.getOrDefault(key, defaultValue);
    }

    public boolean containsKey(K key)
    {
        return map.containsKey(key);
    }

    public int size()
    {
        return map.size();
    }

    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    public Map<K, V> getMap()
    {
        return Map.copyOf(map);
    }

    @Override
    public int hashCode()
    {
        return hashCode;
    }

    public final int hashCode(Object... fields)
    {
        int result = hashCode;

        for (Object o : fields)
        {
            result = 31 * result + Objects.hashCode(o);
        }

        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        else if (obj == null || obj.getClass() != this.getClass()) return false;

        MapLikeData<?, ?> other = (MapLikeData<?, ?>) obj;
        return this.map.equals(other.map);
    }
}