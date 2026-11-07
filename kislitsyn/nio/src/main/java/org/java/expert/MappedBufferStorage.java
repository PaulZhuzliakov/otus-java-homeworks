package org.java.expert;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MappedBufferStorage implements OffHeapStorage {
    private final int bufferSize;

    public MappedBufferStorage(int bufferSize) {
        if (bufferSize < 0) throw new IllegalArgumentException("Buffer size can't be less than 0");

        this.bufferSize = bufferSize;
    }

    @Override
    public ByteBuffer load(String path) throws NoSuchFileException {
        try (FileChannel channel = FileChannel.open(Paths.get(path), StandardOpenOption.READ)) {
            long fileSize = channel.size();

            if (fileSize > bufferSize) throw new IllegalArgumentException("File size is more than buffer size");

            return channel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize);
        } catch (NoSuchFileException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
