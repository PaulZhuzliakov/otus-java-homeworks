package org.java.expert.profiling.agent;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

public final class ProfilingRuntime {

    private static final long NOT_STARTED = Long.MIN_VALUE;

    private static final AtomicLong STARTED_AT_NANOS = new AtomicLong(NOT_STARTED);

    private static long durationNanos;

    private ProfilingRuntime() {
    }

    public static void configure(Duration duration) {
        durationNanos = duration.toNanos();
        STARTED_AT_NANOS.set(NOT_STARTED);
    }

    public static void recordCall(String methodId) {
        long now = System.nanoTime();
        long startedAt = STARTED_AT_NANOS.get();

        if (startedAt == NOT_STARTED) {
            STARTED_AT_NANOS.compareAndSet(NOT_STARTED, now);
            startedAt = STARTED_AT_NANOS.get();
        }

        if (now - startedAt < durationNanos) {
            MethodCallCounter.increment(methodId);
        }
    }
}
