package org.java.expert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CacheImplTest {

    private Cache<String, Integer> cache;

    @BeforeEach
    void setUp() {
        cache = new CacheImpl<>(50);
    }

    @Test
    void shouldRejectNullOnGet() {
        assertThrows(
                NullPointerException.class,
                () -> cache.get(null)
        );
    }

    @Test
    void shouldRejectNullOnPut() {
        assertThrows(
                NullPointerException.class,
                () -> cache.put(null, null)
        );
    }

    @Test
    void shouldRejectZeroCapacity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CacheImpl<String, Integer>(0)
        );
    }

    @Test
    void shouldRejectNegativeCapacity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CacheImpl<String, Integer>(-1)
        );
    }

    @Test
    void shouldEvictLeastRecentlyUsedEntry() {
        Cache<String, Integer> cache = new CacheImpl<>(2);

        cache.put("first", 10);
        cache.put("second", 20);

        cache.get("first");

        cache.put("third", 30);

        assertEquals(10, cache.get("first"));
        assertEquals(30, cache.get("third"));
        assertNull(cache.get("second"));
    }
}
