package org.java.expert.webservicewithlogin.benchmark;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode({
        Mode.Throughput,
        Mode.AverageTime,
        Mode.SampleTime,
        Mode.SingleShotTime
})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
@State(Scope.Thread)
public class PasswordHashBenchmark {

    @Param({"MD5", "SHA-256", "SHA-512"})
    public String algorithm;

    @Param({"1", "1000", "10000"})
    public int hashIterations;

    private MessageDigest messageDigest;
    private byte[] password;

    @Setup(Level.Trial)
    public void setUp() throws NoSuchAlgorithmException {
        messageDigest = MessageDigest.getInstance(algorithm);
        password = "correct horse battery staple".getBytes(StandardCharsets.UTF_8);
    }

    @Benchmark
    public byte[] hashPassword() {
        byte[] hash = password;

        for (int iteration = 0; iteration < hashIterations; iteration++) {
            hash = messageDigest.digest(hash);
        }

        return hash;
    }
}
