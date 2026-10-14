package org.java.expert.webservicewithlogin.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.java.expert.webservicewithlogin.model.User;
import org.java.expert.webservicewithlogin.service.DuplicateLoginException;
import org.java.expert.webservicewithlogin.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class RegistrationControllerTest {

    private MockMvc mockMvc;
    private RegistrationService registrationService;

    @BeforeEach
    void setUp() {
        registrationService = mock(RegistrationService.class);
        var controller = new RegistrationController(registrationService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void registersUser() throws Exception {
        when(registrationService.register("alice", "secret"))
                .thenReturn(user(1L, "alice"));

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

    @Test
    void rejectsDuplicateLogin() throws Exception {
        when(registrationService.register("alice", "secret"))
                .thenReturn(user(1L, "alice"))
                .thenThrow(new DuplicateLoginException("alice"));

        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "login": "alice",
                          "password": "secret"
                        }
                        """);

        mockMvc.perform(request).andExpect(status().isCreated());
        mockMvc.perform(request).andExpect(status().isConflict());
    }

    private static User user(long id, String login) {
        var user = new User();
        user.setId(id);
        user.setLogin(login);
        user.setPasswordHash("encoded-password");
        return user;
    }
}
