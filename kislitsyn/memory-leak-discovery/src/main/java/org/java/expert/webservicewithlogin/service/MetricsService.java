package org.java.expert.webservicewithlogin.service;

import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MetricsService {
    private final MeterRegistry meterRegistry;

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void countRegistrationAttempts(int count) {
        for (int i = 0; i < count; i++) {
            UUID traceToken = UUID.randomUUID();

            meterRegistry.counter("demo.memory.leak", "reg.attempts.trace.id", traceToken.toString()).increment();
        }
    }
}
