package hw09;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.atomic.AtomicLong;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.All)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
@Fork(1)
public class RegistrationBenchmark {

    private RegistrationService service;
    private final AtomicLong counter = new AtomicLong();

    @Setup
    public void setUp() {
        service = new RegistrationService();
    }

    @TearDown
    public void tearDown() {
        service.close();
    }

    @Benchmark
    public long register() {
        return service.register("user-" + counter.incrementAndGet(), "password123");
    }
}
