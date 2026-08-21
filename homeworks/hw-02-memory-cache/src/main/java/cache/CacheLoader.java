package cache;

import java.io.IOException;

/** Загружает значение по ключу, если его нет в кэше. */
@FunctionalInterface
public interface CacheLoader<K, V> {

    V load(K key) throws IOException;
}
