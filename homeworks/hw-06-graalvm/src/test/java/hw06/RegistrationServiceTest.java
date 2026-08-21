package hw06;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RegistrationServiceTest {

    @Autowired
    RegistrationService service;

    @Test
    void registerSavesUserAndReturnsId() {
        long id = service.register("testuser", "testpass");
        assertThat(id).isGreaterThan(0);
    }
}
