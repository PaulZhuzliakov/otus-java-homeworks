package org.example.memorydump.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1)
@Threads(1)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
public class UserControllerBenchmark extends AbstractUserIntegrationBenchmark {

    @Benchmark
    public void testMD5(Blackhole blackhole) throws Exception {
        var response = createUser("MD5");

        blackhole.consume(response.getResponse());
    }

    @Benchmark
    public void testSHA256(Blackhole blackhole) throws Exception {
        var response = createUser("SHA-256");

        blackhole.consume(response.getResponse());
    }

    @Benchmark
    public void testSHA512(Blackhole blackhole) throws Exception {
        var response = createUser("SHA-512");

        blackhole.consume(response.getResponse());
    }
}
