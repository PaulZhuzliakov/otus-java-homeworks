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

    public void countRegistrationAttempts() {
        meterRegistry.counter("demo.memory.leak", "operation", "registration").increment();
    }
}
