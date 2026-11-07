package org.java.expert.webservicewithlogin.problem;

import org.java.expert.webservicewithlogin.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Deliberately executes database queries whose results are ignored.
 *
 * <p>This is a training-only performance problem. It is disabled by default and
 * enabled by the {@code extra-queries} Spring profile.</p>
 */
@Component
public class RedundantQuerySimulator {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedundantQuerySimulator.class);

    private final UserRepository userRepository;
    private final boolean enabled;
    private final int queryCount;

    public RedundantQuerySimulator(
            UserRepository userRepository,
            @Value("${profiling.redundant-queries.enabled:false}") boolean enabled,
            @Value("${profiling.redundant-queries.count:0}") int queryCount
    ) {
        if (queryCount < 0) {
            throw new IllegalArgumentException("profiling.redundant-queries.count must not be negative");
        }

        this.userRepository = userRepository;
        this.enabled = enabled;
        this.queryCount = queryCount;

        if (enabled) {
            LOGGER.warn(
                    "Intentional profiling problem enabled: {} redundant database queries per registration",
                    queryCount
            );
        }
    }

    public void executeRedundantDatabaseQueries(String login) {
        if (!enabled) {
            return;
        }

        for (int queryNumber = 0; queryNumber < queryCount; queryNumber++) {
            userRepository.existsByLogin(login);
        }
    }
}
