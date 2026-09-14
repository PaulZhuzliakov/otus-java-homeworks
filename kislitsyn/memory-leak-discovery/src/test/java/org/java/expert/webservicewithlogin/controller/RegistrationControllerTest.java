package org.java.expert.webservicewithlogin.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.micrometer.core.instrument.Counter;
import org.java.expert.webservicewithlogin.model.User;
import org.java.expert.webservicewithlogin.service.DuplicateLoginException;
import org.java.expert.webservicewithlogin.service.MetricsService;
import org.java.expert.webservicewithlogin.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class RegistrationControllerTest {

    private MockMvc mockMvc;
    private RegistrationService registrationService;
    private MetricsService metricsService;

    @BeforeEach
    void setUp() {
        registrationService = mock(RegistrationService.class);
        metricsService = mock(MetricsService.class);
        var controller = new RegistrationController(registrationService, metricsService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void registersUser() throws Exception {
        when(registrationService.register("alice", "secret"))
                .thenReturn(user(1L, "alice"));

        doNothing().when(metricsService).countRegistrationAttempts(100);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "login": "alice",
                                  "password": "secret"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("alice"));
    }

    @Test
    void rejectsBlankCredentials() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "login": "",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    private static User user(long id, String login) {
        var user = new User();
        user.setId(id);
        user.setLogin(login);
        user.setPasswordHash("encoded-password");
        return user;
    }
}
