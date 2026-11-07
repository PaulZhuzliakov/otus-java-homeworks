package org.java.expert;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class ByteBufferStorage implements OffHeapStorage {
    private static final int CHUNKING_THRESHOLD = 50 * 1024 * 1024;
    private static final int CHUNK_SIZE = 5 * 1024 * 1024;

    private final int bufferSize;

    public ByteBufferStorage(int bufferSize) {
        if (bufferSize < 0) throw new IllegalArgumentException("Buffer size can't be less than 0");

        this.bufferSize = bufferSize;
    }

    @Override
    public Buffer load(String path) throws NoSuchFileException {
        try (FileChannel channel = FileChannel.open(Paths.get(path), StandardOpenOption.READ);) {
            long fileSize = channel.size();

            if (fileSize > bufferSize) throw new IllegalArgumentException("File size is more than buffer size");

            ByteBuffer buffer = ByteBuffer.allocateDirect((int) fileSize);

            if (fileSize > CHUNKING_THRESHOLD) {
                chunkedRead(channel, buffer);
            } else {
                fullRead(channel, buffer);
            }

            return buffer.flip();
        } catch (NoSuchFileException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void chunkedRead(FileChannel channel, ByteBuffer buffer) throws IOException {
        assert buffer.limit() == buffer.capacity();

        while (buffer.position() < buffer.capacity()) {
            int bytesToRead = Math.min(
                    CHUNK_SIZE,
                    buffer.capacity() - buffer.position()
            );

            buffer.limit(buffer.position() + bytesToRead);
            channel.read(buffer);
        }
    }

    private void fullRead(FileChannel channel, ByteBuffer buffer) throws IOException {
        assert buffer.limit() == buffer.capacity();

        while (buffer.hasRemaining()) {
            channel.read(buffer);
        }
    }
}
