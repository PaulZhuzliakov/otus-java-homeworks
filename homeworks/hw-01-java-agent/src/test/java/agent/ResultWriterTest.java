package agent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void writesSnapshotToFile() throws Exception {
        Map<String, Long> snapshot = new HashMap<>();
        snapshot.put("demo.App.fastMethod()", 10L);
        snapshot.put("demo.App.slowMethod()", 5L);
        snapshot.put("demo.App.neverCalled()", 0L);
        Path output = tempDir.resolve("profiler-result.txt");

        ResultWriter.writeSnapshot(output, snapshot);

        List<String> lines = Files.readAllLines(output);
        assertEquals(3, lines.size());
        assertTrue(lines.contains("demo.App.fastMethod()=10"));
        assertTrue(lines.contains("demo.App.slowMethod()=5"));
        assertTrue(lines.contains("demo.App.neverCalled()=0"));
    }
}
