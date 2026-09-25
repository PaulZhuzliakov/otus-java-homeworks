package org.java.expert;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class CacheImpl<K, V> implements Cache<K, V> {

    private final ReferenceQueue<V> referenceQueue;
    private final Map<K, WeakReference<V>> referenceByKey;
    private final Map<Reference<?>, K> keyByReference;

    private final int capacity;

    public CacheImpl(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }

        this.referenceQueue = new ReferenceQueue<>();
        this.referenceByKey = new LinkedHashMap<>(capacity, .75f, true);
        this.keyByReference = new HashMap<>();

        this.capacity = capacity;
    }

    @Override
    public void put(K key, V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);

        removeCollectedEntries();

        WeakReference<V> ref = new WeakReference<>(value, referenceQueue);
        WeakReference<V> previousRef = referenceByKey.put(key, ref);
        if (previousRef != null) {
            keyByReference.remove(previousRef);
        }
        keyByReference.put(ref, key);

        evictIfNecessary();
    }

    @Override
    public V get(K key) {
        Objects.requireNonNull(key);

        removeCollectedEntries();

        WeakReference<V> ref = referenceByKey.get(key);

        if (ref == null) {
            return null;
        }

        return ref.get();
    }

    private void evictIfNecessary() {
        while (referenceByKey.size() > capacity) {
            Map.Entry<K, WeakReference<V>> lru = referenceByKey.entrySet().iterator().next();
            referenceByKey.remove(lru.getKey());
            keyByReference.remove(lru.getValue());
        }
    }

    private void removeCollectedEntries() {
        Reference<? extends V> collectedRef;

        while ((collectedRef = referenceQueue.poll()) != null) {
            K key = keyByReference.remove(collectedRef);

            if (key != null) {
                referenceByKey.remove(key, collectedRef);
            }
        }
    }
}
