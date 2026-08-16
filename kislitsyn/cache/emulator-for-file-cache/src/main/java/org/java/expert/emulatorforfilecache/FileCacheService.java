package org.java.expert.emulatorforfilecache;

import org.java.expert.Cache;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;

@Service
public class FileCacheService {

    private final Cache<String, String> cache;
    private Path directory;

    public FileCacheService(Cache<String, String> cache) {
        this.cache = cache;
    }

    public Path configureDirectory(Path directory) {
        Objects.requireNonNull(directory, "Directory must not be null");

        if (this.directory != null) {
            throw new IllegalStateException(
                    "Cache directory is already configured: " + this.directory
            );
        }

        Path normalizedDirectory = directory.toAbsolutePath().normalize();

        if (!Files.exists(normalizedDirectory)) {
            throw new IllegalArgumentException(
                    "Directory does not exist: " + normalizedDirectory
            );
        }

        if (!Files.isDirectory(normalizedDirectory)) {
            throw new IllegalArgumentException(
                    "Path is not a directory: " + normalizedDirectory
            );
        }

        this.directory = normalizedDirectory;
        return normalizedDirectory;
    }

    public String loadFile(String fileName) throws IOException {
        String cacheKey = normalizeFileName(fileName);
        Path file = resolveFile(cacheKey);

        if (!Files.isRegularFile(file)) {
            throw new NoSuchFileException(file.toString());
        }

        String content = Files.readString(file, StandardCharsets.UTF_8);
        cache.put(cacheKey, content);

        return content;
    }

    public String readCachedFile(String fileName) {
        ensureDirectoryConfigured();

        String cacheKey = normalizeFileName(fileName);
        String content = cache.get(cacheKey);

        if (content == null) {
            throw new IllegalStateException(
                    "File is not cached: " + fileName
            );
        }

        return content;
    }

    private String normalizeFileName(String fileName) {
        Objects.requireNonNull(fileName, "File name must not be null");

        if (fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "File name must not be blank"
            );
        }

        Path relativePath = Path.of(fileName).normalize();

        if (relativePath.isAbsolute()) {
            throw new IllegalArgumentException(
                    "File name must be relative to the configured directory"
            );
        }

        if (relativePath.toString().isBlank()
                || relativePath.getNameCount() != 1
                || relativePath.toString().equals("..")) {
            throw new IllegalArgumentException(
                    "File name must not contain directory traversal"
            );
        }

        return relativePath.toString();
    }

    private Path resolveFile(String fileName) {
        ensureDirectoryConfigured();

        Path file = directory.resolve(fileName).normalize();

        if (!file.startsWith(directory)) {
            throw new IllegalArgumentException(
                    "File must be inside the configured directory"
            );
        }

        return file;
    }

    private void ensureDirectoryConfigured() {
        if (directory == null) {
            throw new IllegalStateException(
                    "Cache directory is not configured"
            );
        }
    }
}
