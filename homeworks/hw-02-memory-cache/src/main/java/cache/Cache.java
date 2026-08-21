package cache;

import java.io.IOException;

/** Кэш «ключ-значение». */
public interface Cache<K, V> {

    V get(K key);

    void put(K key, V value);

    void clear();

    default V getOrLoad(K key, CacheLoader<K, V> loader) throws IOException {
        V value = get(key);
        if (value == null) {
            value = loader.load(key);
            put(key, value);
        }
        return value;
    }
}
