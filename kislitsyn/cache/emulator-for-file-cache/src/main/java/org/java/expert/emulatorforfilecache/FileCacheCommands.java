package org.java.expert.emulatorforfilecache;

import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Component
public class FileCacheCommands {

    private static final String COMMAND_GROUP = "File Cache";

    private final FileCacheService fileCacheService;

    public FileCacheCommands(FileCacheService fileCacheService) {
        this.fileCacheService = fileCacheService;
    }

    @Command(
            name = "directory",
            description = "Configure the directory containing cacheable files",
            group = COMMAND_GROUP
    )
    public String configureDirectory(
            @Option(
                    shortName = 'd',
                    longName = "path",
                    description = "Path to the directory",
                    required = true
            ) String directory
    ) {
        Path configuredDirectory =
                fileCacheService.configureDirectory(Path.of(directory));

        return "Cache directory: " + configuredDirectory;
    }

    @Command(
            name = "load",
            description = "Load a file from the configured directory into the cache",
            group = COMMAND_GROUP
    )
    public String loadFile(
            @Option(
                    shortName = 'f',
                    longName = "file",
                    description = "File name relative to the configured directory",
                    required = true
            ) String fileName
    ) throws IOException {
        String content = fileCacheService.loadFile(fileName);

        return "Loaded '%s' into cache (%d characters)"
                .formatted(fileName, content.length());
    }

    @Command(
            name = "read",
            description = "Read a previously loaded file from the cache",
            group = COMMAND_GROUP
    )
    public String readFile(
            @Option(
                    shortName = 'f',
                    longName = "file",
                    description = "Cached file name",
                    required = true
            ) String fileName
    ) {
        return fileCacheService.readCachedFile(fileName);
    }
}
