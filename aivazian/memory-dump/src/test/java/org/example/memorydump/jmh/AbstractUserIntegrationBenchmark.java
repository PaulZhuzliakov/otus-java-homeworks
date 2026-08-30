package org.example.memorydump.jmh;

import org.example.memorydump.MemoryDumpApplication;
import org.example.memorydump.dto.UserCreateRequest;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractUserIntegrationBenchmark {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicLong userCounter = new AtomicLong();

    private ConfigurableApplicationContext context;
    private MockMvc mockMvc;

    @Setup(Level.Trial)
    public void setup() {
        SpringApplication app = new SpringApplication(MemoryDumpApplication.class);
        app.setDefaultProperties(Map.of("server.port", "0"));
        context = app.run();

        WebApplicationContext webContext = (WebApplicationContext) context;

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webContext)
                .build();
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        context.close();
    }

    protected MvcResult createUser(String algorithm) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/users")  // ← POST запрос
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest(algorithm))
        ).andReturn();
    }

    private String createRequest(String algorithm) {
        UserCreateRequest userCreateRequest = new UserCreateRequest("user-" + nextId(), "12345678", algorithm);
        return objectMapper.writeValueAsString(userCreateRequest);
    }

    private Long nextId() {
        return userCounter.incrementAndGet();
    }
}
