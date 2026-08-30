package org.example.memorydump.jmh;

import org.example.memorydump.service.HashService;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 20, time = 1)
@Fork(5)
@Threads(1)
@State(Scope.Benchmark)
public class HashServiceBenchmark {
    private HashService hashService;

    @Setup(Level.Trial)
    public void setUp() {
        hashService = new HashService();
    }

    @Benchmark
    public void testMD5(Blackhole blackhole) {
        String hashPassword = hashService.hashPassword("123456789", "MD5");
        blackhole.consume(hashPassword);
    }

    @Benchmark
    public void testSHA256(Blackhole blackhole) {
        String hashPassword = hashService.hashPassword("123456789", "SHA-256");
        blackhole.consume(hashPassword);
    }

    @Benchmark
    public void testSHA512(Blackhole blackhole) {
        String hashPassword = hashService.hashPassword("123456789", "SHA-512");
        blackhole.consume(hashPassword);
    }
}
