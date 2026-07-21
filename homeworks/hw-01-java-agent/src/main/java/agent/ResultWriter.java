package agent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// По истечении времени сбора пишет значения счётчиков в файл.
public final class ResultWriter {

    public static final Path OUTPUT = Path.of("profiler-result.txt");

    private ResultWriter() {
    }

    public static void scheduleDump(long delayMillis) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(delayMillis);
                List<String> lines = new ArrayList<>();
                Counters.getAll().forEach((method, count) -> lines.add(method + "=" + count));
                Files.write(OUTPUT, lines);
                System.out.println("profiler: результаты записаны в " + OUTPUT.toAbsolutePath());
            } catch (Exception e) {
                System.err.println("profiler: не удалось записать результаты: " + e.getMessage());
            }
        });
        thread.start();
    }
}
