package cache.menu;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmulatorTest {

    @TempDir
    Path tempDir;

    @Test
    void getFileReturnsFileContent() throws IOException {
        Files.writeString(tempDir.resolve("Names.txt"), "Иван\nМария\n");
        Emulator emulator = new Emulator();
        emulator.setDirectory(tempDir.toString());

        assertEquals("Иван\nМария\n", emulator.getFile("Names.txt"));
    }

    @Test
    void getFileTakesContentFromCacheOnSecondCall() throws IOException {
        Path file = tempDir.resolve("Names.txt");
        Files.writeString(file, "Иван\nМария\n");
        Emulator emulator = new Emulator();
        emulator.setDirectory(tempDir.toString());
        emulator.getFile("Names.txt");

        Files.delete(file); // удаляем файл — содержимое должно взяться из кэша

        assertEquals("Иван\nМария\n", emulator.getFile("Names.txt"));
    }

    @Test
    void getFileWithoutDirectoryThrows() {
        Emulator emulator = new Emulator();

        assertThrows(IllegalStateException.class, () -> emulator.getFile("Names.txt"));
    }

    @Test
    void getFileAfterDirectoryChangeNotFromOldCache() throws IOException {
        Files.writeString(tempDir.resolve("Names.txt"), "Иван\nМария\n");
        Emulator emulator = new Emulator();
        emulator.setDirectory(tempDir.toString());
        emulator.getFile("Names.txt");

        Path otherDir = Files.createDirectory(tempDir.resolve("other"));
        Files.writeString(otherDir.resolve("Names.txt"), "Пётр\n");
        emulator.setDirectory(otherDir.toString());

        assertEquals("Пётр\n", emulator.getFile("Names.txt"));
    }
}
