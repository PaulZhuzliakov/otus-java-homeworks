package org.java.expert.webservicewithlogin.problem;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.java.expert.webservicewithlogin.repository.UserRepository;
import org.junit.jupiter.api.Test;

class RedundantQuerySimulatorTest {

    private final UserRepository userRepository = mock(UserRepository.class);

    @Test
    void doesNothingWhenProblemIsDisabled() {
        var simulator = new RedundantQuerySimulator(userRepository, false, 100);

        simulator.executeRedundantDatabaseQueries("alice");

        verify(userRepository, never()).existsByLogin("alice");
    }

    @Test
    void executesConfiguredNumberOfRedundantQueries() {
        var simulator = new RedundantQuerySimulator(userRepository, true, 5);

        simulator.executeRedundantDatabaseQueries("alice");

        verify(userRepository, times(5)).existsByLogin("alice");
    }

    @Test
    void rejectsNegativeQueryCount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RedundantQuerySimulator(userRepository, true, -1)
        );
    }
}
