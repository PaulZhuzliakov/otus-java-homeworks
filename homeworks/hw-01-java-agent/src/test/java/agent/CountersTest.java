package agent;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CountersTest {

    @Test
    void incrementCountsCalls() {
        Counters.register("demo.App.testMethod1()");
        Counters.register("demo.App.testMethod2()");

        Counters.increment("demo.App.testMethod1()");
        Counters.increment("demo.App.testMethod1()");
        Counters.increment("demo.App.testMethod2()");

        assertEquals(2, Counters.snapshot().get("demo.App.testMethod1()"));
        assertEquals(1, Counters.snapshot().get("demo.App.testMethod2()"));
    }

    @Test
    void registeredWithoutCallsGivesZero() {
        Counters.register("demo.App.notCalled()");

        assertEquals(0, Counters.snapshot().get("demo.App.notCalled()"));
    }

    @Test
    void concurrentIncrementsAreNotLost() throws Exception {
        Counters.register("demo.App.concurrent()");
        int threads = 8;
        int incrementsPerThread = 10_000;

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    Counters.increment("demo.App.concurrent()");
                }
            });
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS));

        assertEquals(threads * incrementsPerThread,
                Counters.snapshot().get("demo.App.concurrent()").longValue());
    }
}
