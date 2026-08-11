package agent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// По истечении времени сбора пишет значения счётчиков в файл.
public final class ResultWriter {

    public static final Path OUTPUT = Path.of("profiler-result.txt");

    private ResultWriter() {
    }

    public static void scheduleDump(long delayMillis) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(delayMillis);
                Counters.stop();
                writeSnapshot(OUTPUT, Counters.snapshot());
                System.out.println("profiler: результаты записаны в " + OUTPUT.toAbsolutePath());
            } catch (Exception e) {
                System.err.println("profiler: не удалось записать результаты: " + e.getMessage());
            }
        });
        // daemon, чтобы не удерживать JVM после завершения приложения
        thread.setDaemon(true);
        thread.start();
    }

    static void writeSnapshot(Path output, Map<String, Long> snapshot) throws IOException {
        List<String> lines = new ArrayList<>();
        snapshot.forEach((method, count) -> lines.add(method + "=" + count));
        Files.write(output, lines);
    }
}
