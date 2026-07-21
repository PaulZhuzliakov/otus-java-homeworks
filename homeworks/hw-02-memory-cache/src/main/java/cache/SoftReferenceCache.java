package cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/** Кэш на SoftReference: значения живут, пока хватает памяти. */
public class SoftReferenceCache<K, V> implements Cache<K, V> {

    private final Map<K, SoftReference<V>> entries = new HashMap<>();

    @Override
    public V get(K key) {
        SoftReference<V> ref = entries.get(key);
        return ref == null ? null : ref.get();
    }

    @Override
    public void put(K key, V value) {
        entries.put(key, new SoftReference<>(value));
    }

    @Override
    public void clear() {
        entries.clear();
    }
}
