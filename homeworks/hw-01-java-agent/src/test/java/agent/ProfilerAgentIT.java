package agent;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

// Запуск демо через -javaagent, проверяем счёт перегрузок и нули
class ProfilerAgentIT {

    @Test
    void agentCountsOverloadsAndNeverCalled() throws Exception {
        Path workDir = Files.createTempDirectory("profiler-it-");
        Path classesDir = Path.of("target/classes").toAbsolutePath();
        Path agentJar = Path.of("target/hw-01-java-agent-1.0-SNAPSHOT.jar").toAbsolutePath();
        assertTrue(Files.exists(agentJar), "jar агента не собран: запустите mvn package");

        Path config = workDir.resolve("config.xml");
        Files.writeString(config, """
                <profiler>
                    <duration>2</duration>
                    <method>demo.App.fastMethod</method>
                    <method>demo.App.slowMethod</method>
                    <method>demo.App.overloadedMethod(int)</method>
                    <method>demo.App.overloadedMethod(java.lang.String)</method>
                    <method>demo.App.neverCalled</method>
                </profiler>
                """);

        ProcessBuilder builder = new ProcessBuilder(
                "java",
                "-javaagent:" + agentJar + "=" + config,
                "-cp", classesDir.toString(),
                "demo.App"
        );
        builder.directory(workDir.toFile());
        builder.redirectErrorStream(true);
        Process process = builder.start();

        try {
            Path resultFile = workDir.resolve("profiler-result.txt");
            boolean exists = waitForFile(resultFile, 15);

            assertTrue(exists, "файл profiler-result.txt не появился");

            List<String> lines = Files.readAllLines(resultFile);
            String text = String.join("\n", lines);
            assertTrue(text.contains("demo.App.overloadedMethod(int)="));
            assertTrue(text.contains("demo.App.overloadedMethod(java.lang.String)="));
            assertTrue(text.contains("demo.App.neverCalled()=0"));
            assertTrue(text.contains("demo.App.fastMethod()="));
        } finally {
            process.destroyForcibly();
            process.waitFor();
        }
    }

    private boolean waitForFile(Path path, long timeoutSeconds) throws InterruptedException {
        File file = path.toFile();
        long end = System.currentTimeMillis() + timeoutSeconds * 1000;
        while (System.currentTimeMillis() < end) {
            if (file.exists() && file.length() > 0) {
                return true;
            }
            Thread.sleep(200);
        }
        return false;
    }
}
