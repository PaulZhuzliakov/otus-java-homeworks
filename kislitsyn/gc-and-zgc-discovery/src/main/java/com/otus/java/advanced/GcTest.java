package com.otus.java.advanced;

import java.util.ArrayDeque;
import java.util.Deque;

public class GcTest {
    private static final int CHUNK_SIZE_BYTES = 64 * 1024;
    private static final int TOTAL_ALLOCATION_HEAP_MULTIPLIER = 3;
    private static final long ALLOCATION_BATCH_BYTES = 32L * 1024 * 1024;
    private static final long BATCH_PAUSE_MILLIS = 20;
    private static final Deque<byte[]> ALLOCATIONS = new ArrayDeque<>();

    public static void main(String[] args) throws InterruptedException {
        long maxHeapBytes = Runtime.getRuntime().maxMemory();
        long liveSetLimitBytes = maxHeapBytes / 2;
        // переменная задает длительность работы GC
        // подход с фиксированным количеством больших итераций не сработал
        // тут мы пытаемся суммарно аллоцировать 6 Гб
        // при этом хип 2 Гб, аллоцировано в моменте не более 1 Гб
        long totalAllocationLimitBytes = Math.multiplyExact(
                maxHeapBytes,
                TOTAL_ALLOCATION_HEAP_MULTIPLIER
        );

        System.out.printf(
                "Max heap: %d MiB, live-set limit: %d MiB, total allocation: %d MiB%n",
                toMiB(maxHeapBytes),
                toMiB(liveSetLimitBytes),
                toMiB(totalAllocationLimitBytes)
        );

        allocate(liveSetLimitBytes, totalAllocationLimitBytes);
    }

    private static void allocate(
            long liveSetLimitBytes,
            long totalAllocationLimitBytes
    ) throws InterruptedException {
        // суммарный размер массивов на которые хранятся ссылки в очереди
        long liveSetBytes = 0;
        long totalAllocatedBytes = 0;
        long nextProgressReportBytes = liveSetLimitBytes;

        while (totalAllocatedBytes < totalAllocationLimitBytes) {
            // защита от превышения лимита 1 Гб
            while (liveSetBytes + CHUNK_SIZE_BYTES > liveSetLimitBytes) {
                liveSetBytes -= ALLOCATIONS.removeFirst().length;
            }

            ALLOCATIONS.addLast(new byte[CHUNK_SIZE_BYTES]);
            liveSetBytes += CHUNK_SIZE_BYTES;
            totalAllocatedBytes += CHUNK_SIZE_BYTES;

            // немного замедлим основной поток после каждых 32 Мб, чтобы снизить скорость аллокации
            // без паузы приложение выделяет память быстрее чем G1 освобождает
            if (totalAllocatedBytes % ALLOCATION_BATCH_BYTES == 0) {
                Thread.sleep(BATCH_PAUSE_MILLIS);
            }

            if (totalAllocatedBytes >= nextProgressReportBytes) {
                System.out.printf(
                        "Allocated: %d MiB, retained: %d MiB, used heap: %d MiB%n",
                        toMiB(totalAllocatedBytes),
                        toMiB(liveSetBytes),
                        toMiB(usedHeapBytes())
                );
                nextProgressReportBytes += liveSetLimitBytes;
            }
        }
    }

    private static long usedHeapBytes() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private static long toMiB(long bytes) {
        return bytes / (1024 * 1024);
    }
}
