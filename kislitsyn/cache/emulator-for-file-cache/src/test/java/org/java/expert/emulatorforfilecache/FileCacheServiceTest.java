package org.java.expert.emulatorforfilecache;

import org.java.expert.CacheImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileCacheServiceTest {

    @TempDir
    Path temporaryDirectory;

    private FileCacheService service;

    @BeforeEach
    void setUp() {
        service = new FileCacheService(new CacheImpl<>(10));
    }

    @Test
    void shouldLoadFileAndReadItsContentFromCache() throws IOException {
        Path file = temporaryDirectory.resolve("Names.txt");
        Files.writeString(file, "Alice\nBob");
        service.configureDirectory(temporaryDirectory);

        String loadedContent = service.loadFile("Names.txt");

        assertEquals("Alice\nBob", loadedContent);
        assertEquals(loadedContent, service.readCachedFile("Names.txt"));
    }

    @Test
    void shouldRequireDirectoryBeforeLoadingFile() {
        assertThrows(
                IllegalStateException.class,
                () -> service.loadFile("Names.txt")
        );
    }

    @Test
    void shouldRejectMissingDirectory() {
        Path missingDirectory = temporaryDirectory.resolve("missing");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.configureDirectory(missingDirectory)
        );
    }

    @Test
    void shouldRejectConfiguringDirectoryMoreThanOnce() {
        service.configureDirectory(temporaryDirectory);

        assertThrows(
                IllegalStateException.class,
                () -> service.configureDirectory(temporaryDirectory)
        );
    }

    @Test
    void shouldRejectPathOutsideConfiguredDirectory() {
        service.configureDirectory(temporaryDirectory);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.loadFile("../Names.txt")
        );
    }

    @Test
    void shouldRejectReadingPathOutsideConfiguredDirectory() {
        service.configureDirectory(temporaryDirectory);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.readCachedFile("../Names.txt")
        );
    }

    @Test
    void shouldRejectMissingFile() {
        service.configureDirectory(temporaryDirectory);

        assertThrows(
                NoSuchFileException.class,
                () -> service.loadFile("missing.txt")
        );
    }

    @Test
    void shouldRejectReadingFileThatWasNotLoaded() {
        service.configureDirectory(temporaryDirectory);

        assertThrows(
                IllegalStateException.class,
                () -> service.readCachedFile("Names.txt")
        );
    }
}
