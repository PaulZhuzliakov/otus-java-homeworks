package org.java.expert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ByteBufferStorageTest {

    @TempDir
    Path tempDir;

    private ByteBufferStorage storage;

    @BeforeEach
    void setUp() {
        storage = new ByteBufferStorage(1024);
    }

    @Test
    void shouldLoadFile() throws IOException {
        byte[] expected = "test data".getBytes(StandardCharsets.UTF_8);
        Path file = tempDir.resolve("test.txt");
        Files.write(file, expected);

        ByteBuffer buffer = (ByteBuffer) storage.load(file.toString());
        byte[] actual = new byte[buffer.remaining()];
        buffer.get(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    void shouldThrowExceptionWhenFileDoesNotExist() {
        Path missingFile = tempDir.resolve("missing.txt");

        assertThrows(
                NoSuchFileException.class,
                () -> storage.load(missingFile.toString())
        );
    }
}
