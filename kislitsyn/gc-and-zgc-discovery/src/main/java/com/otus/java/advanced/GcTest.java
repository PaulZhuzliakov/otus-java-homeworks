package com.otus.java.advanced;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class GcTest {
    private static final int CHUNK_SIZE_BYTES = 256 * 1024;
    private static final List<byte[]> ALLOCATIONS = new ArrayList<>();

    public static void main(String[] args) {
        long maxHeapBytes = Runtime.getRuntime().maxMemory();
        long targetAllocationBytes = maxHeapBytes / 2;

        System.out.printf(
                "Max heap: %d MiB, target allocation: %d MiB%n",
                toMiB(maxHeapBytes),
                toMiB(targetAllocationBytes)
        );

        for (int step = 1; step <= 5; step++) {
            long allocatedBytes = allocate(targetAllocationBytes);

            System.out.printf(
                    "Step: %d: allocated payload: %d MiB, used heap: %d MiB%n",
                    step,
                    toMiB(allocatedBytes),
                    toMiB(usedHeapBytes())
            );

            ALLOCATIONS.clear();
        }
    }

    private static long allocate(long targetBytes) {
        long allocatedBytes = 0;

        while (allocatedBytes < targetBytes) {
            int allocationSize = (int) Math.min(CHUNK_SIZE_BYTES, targetBytes - allocatedBytes);
            ALLOCATIONS.add(new byte[allocationSize]);
            allocatedBytes += allocationSize;
        }

        return allocatedBytes;
    }

    private static long usedHeapBytes() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private static long toMiB(long bytes) {
        return bytes / (1024 * 1024);
    }
}
