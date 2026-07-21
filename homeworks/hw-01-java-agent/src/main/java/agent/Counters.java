package agent;

import java.util.HashMap;
import java.util.Map;

// Счётчики входов в методы
public final class Counters {

    private static final Map<String, Integer> counters = new HashMap<>();

    private Counters() {
    }

    public static void increment(String method) {
        counters.put(method, counters.getOrDefault(method, 0) + 1);
    }

    public static Map<String, Integer> getAll() {
        return counters;
    }
}
