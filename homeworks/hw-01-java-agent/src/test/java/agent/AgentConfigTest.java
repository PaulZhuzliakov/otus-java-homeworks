package agent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AgentConfigTest {

    @TempDir
    Path tempDir;

    @Test
    void loadsMethodsAndDuration() throws Exception {
        Path config = tempDir.resolve("config.xml");
        Files.writeString(config, """
                <profiler>
                    <duration>5</duration>
                    <method>demo.App.fastMethod</method>
                    <method>demo.App.slowMethod</method>
                </profiler>
                """);

        AgentConfig agentConfig = AgentConfig.load(config.toString());

        assertEquals(List.of("demo.App.fastMethod", "demo.App.slowMethod"), agentConfig.getMethods());
        assertEquals(5000, agentConfig.getDurationMillis());
    }

    @Test
    void missingFileThrows() {
        assertThrows(Exception.class, () -> AgentConfig.load(tempDir.resolve("nope.xml").toString()));
    }
}
