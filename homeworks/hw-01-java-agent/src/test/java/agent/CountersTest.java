package agent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CountersTest {

    @Test
    void incrementCountsCalls() {
        Counters.increment("demo.App.testMethod1");
        Counters.increment("demo.App.testMethod1");
        Counters.increment("demo.App.testMethod2");

        assertEquals(2, Counters.getAll().get("demo.App.testMethod1"));
        assertEquals(1, Counters.getAll().get("demo.App.testMethod2"));
    }
}
