package cache;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Кэш на WeakReference: значение удаляется, как только на него нет сильных ссылок. */
public class WeakReferenceCache<K, V> implements Cache<K, V> {

    private final Map<K, WeakReference<V>> entries = new ConcurrentHashMap<>();

    @Override
    public V get(K key) {
        WeakReference<V> ref = entries.get(key);
        return ref == null ? null : ref.get();
    }

    @Override
    public void put(K key, V value) {
        entries.put(key, new WeakReference<>(value));
    }

    @Override
    public void clear() {
        entries.clear();
    }
}
