package agent;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

// Счётчики входов в методы. ConcurrentHashMap + LongAdder, чтобы не терять инкременты из разных потоков
public final class Counters {

    private static final Map<String, LongAdder> counters = new ConcurrentHashMap<>();
    private static volatile boolean running = true;

    private Counters() {
    }

    // регистрируем метод из конфига, чтобы он попал в результат даже без вызовов
    public static void register(String method) {
        counters.putIfAbsent(method, new LongAdder());
    }

    public static void increment(String method) {
        if (!running) {
            return;
        }
        LongAdder counter = counters.get(method);
        if (counter != null) {
            counter.increment();
        }
    }

    // останавливаем сбор: дальнейшие инкременты игнорируются
    public static void stop() {
        running = false;
    }

    // снимок значений счётчиков
    public static Map<String, Long> snapshot() {
        Map<String, Long> result = new TreeMap<>();
        counters.forEach((method, counter) -> result.put(method, counter.sum()));
        return result;
    }
}
