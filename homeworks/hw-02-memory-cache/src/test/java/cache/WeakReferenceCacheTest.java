package cache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WeakReferenceCacheTest {

    @Test
    void putAndGet() {
        Cache<String, String> cache = new WeakReferenceCache<>();
        cache.put("a", "alpha");

        assertEquals("alpha", cache.get("a"));
    }

    @Test
    void getMissingReturnsNull() {
        Cache<String, String> cache = new WeakReferenceCache<>();

        assertNull(cache.get("missing"));
    }

    @Test
    void clearRemovesEverything() {
        Cache<String, String> cache = new WeakReferenceCache<>();
        cache.put("a", "alpha");
        cache.put("b", "beta");

        cache.clear();

        assertNull(cache.get("a"));
        assertNull(cache.get("b"));
    }
}
