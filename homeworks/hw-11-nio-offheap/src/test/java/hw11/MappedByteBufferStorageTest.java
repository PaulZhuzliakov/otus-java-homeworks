package hw11;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MappedByteBufferStorageTest {

    @TempDir
    Path temp;

    @Test
    void loadAndReadAll() throws IOException {
        Path file = temp.resolve("data.bin");
        Files.write(file, new byte[]{1, 2, 3, 4, 5});

        MappedByteBufferStorage storage = new MappedByteBufferStorage(100);
        storage.load(file);

        assertEquals(5, storage.size());
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5}, storage.readAll());
    }

    @Test
    void readByteByPosition() throws IOException {
        Path file = temp.resolve("data.bin");
        Files.write(file, new byte[]{10, 20, 30});

        MappedByteBufferStorage storage = new MappedByteBufferStorage(100);
        storage.load(file);

        assertEquals(20, storage.readByte(1));
    }

    @Test
    void missingFileThrowsNoSuchFile() {
        MappedByteBufferStorage storage = new MappedByteBufferStorage(100);

        assertThrows(NoSuchFileException.class, () -> storage.load(temp.resolve("нет такого.bin")));
    }

    @Test
    void fileBiggerThanStorageThrows() throws IOException {
        Path file = temp.resolve("big.bin");
        Files.write(file, new byte[10]);

        MappedByteBufferStorage storage = new MappedByteBufferStorage(4);

        assertThrows(IllegalArgumentException.class, () -> storage.load(file));
    }

    @Test
    void fileExactlyOfCapacityLoads() throws IOException {
        Path file = temp.resolve("exact.bin");
        Files.write(file, new byte[10]);

        MappedByteBufferStorage storage = new MappedByteBufferStorage(10);
        storage.load(file);

        assertEquals(10, storage.size());
    }

    @Test
    void readBeforeLoadThrows() {
        MappedByteBufferStorage storage = new MappedByteBufferStorage(100);

        assertThrows(IllegalStateException.class, storage::readAll);
    }
}
