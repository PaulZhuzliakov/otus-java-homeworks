package cache;

/** Кэш «ключ-значение». */
public interface Cache<K, V> {

    V get(K key);

    void put(K key, V value);

    void clear();
}
