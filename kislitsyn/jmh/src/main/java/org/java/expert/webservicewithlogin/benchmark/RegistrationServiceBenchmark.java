package org.java.expert.webservicewithlogin.benchmark;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.java.expert.webservicewithlogin.WebServiceWithLoginApplication;
import org.java.expert.webservicewithlogin.model.User;
import org.java.expert.webservicewithlogin.service.RegistrationService;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@BenchmarkMode({
        Mode.Throughput,
        Mode.AverageTime,
        Mode.SampleTime,
        Mode.SingleShotTime
})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
@State(Scope.Benchmark)
public class RegistrationServiceBenchmark {

    private static final String PASSWORD = "qwerty12345";

    private final AtomicLong loginSequence = new AtomicLong();

    private ConfigurableApplicationContext applicationContext;
    private RegistrationService registrationService;

    @Setup(Level.Trial)
    public void setUp() {
        applicationContext = new SpringApplicationBuilder(WebServiceWithLoginApplication.class)
                .web(WebApplicationType.NONE)
                .properties(
                        "spring.main.banner-mode=off",
                        "logging.level.root=ERROR",
                        "spring.jpa.show-sql=false",
                        "spring.datasource.url=jdbc:h2:mem:jmh-benchmark"
                )
                .run();

        registrationService = applicationContext.getBean(RegistrationService.class);
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        applicationContext.close();
    }

    @Benchmark
    public User registerUser() {
        String login = "jmh-user-" + loginSequence.incrementAndGet();
        return registrationService.register(login, PASSWORD);
    }
}
