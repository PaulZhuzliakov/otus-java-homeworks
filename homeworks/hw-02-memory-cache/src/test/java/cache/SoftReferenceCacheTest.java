package cache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SoftReferenceCacheTest {

    @Test
    void putAndGet() {
        Cache<String, String> cache = new SoftReferenceCache<>();
        cache.put("a", "alpha");

        assertEquals("alpha", cache.get("a"));
    }

    @Test
    void getMissingReturnsNull() {
        Cache<String, String> cache = new SoftReferenceCache<>();

        assertNull(cache.get("missing"));
    }

    @Test
    void clearRemovesEverything() {
        Cache<String, String> cache = new SoftReferenceCache<>();
        cache.put("a", "alpha");
        cache.put("b", "beta");

        cache.clear();

        assertNull(cache.get("a"));
        assertNull(cache.get("b"));
    }

    @Test
    void putOverwritesPreviousValue() {
        Cache<String, String> cache = new SoftReferenceCache<>();
        cache.put("a", "alpha");
        cache.put("a", "new");

        assertEquals("new", cache.get("a"));
    }
}
