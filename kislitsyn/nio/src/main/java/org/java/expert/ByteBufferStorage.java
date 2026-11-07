package org.java.expert;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class ByteBufferStorage implements OffHeapStorage {
    private final int CHUNKING_THRESHOLD = 50 * 1024 * 1024; // 100Mb
    private final int CHUCK_SIZE = 5 * 1024 * 1024; // 5Mb

    private int bufferSize = 50 * 1024 * 1024; // 100Mb

    public ByteBufferStorage(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public Buffer load(String path) throws NoSuchFileException {
        try (FileChannel channel = FileChannel.open(Paths.get(path), StandardOpenOption.READ);) {
            long fileSize = channel.size();
            if (fileSize > bufferSize) throw new IllegalArgumentException("File size is more than buffer size");

            int actualNeededBufferSize = Math.toIntExact(Math.min(bufferSize, fileSize));
            ByteBuffer buffer = ByteBuffer.allocateDirect(actualNeededBufferSize);

            boolean isChunkedRead = fileSize > actualNeededBufferSize;

            if (isChunkedRead) {
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
            int bytesRead = Math.min(
                    CHUCK_SIZE,
                    buffer.capacity() - buffer.position()
            );

            buffer.limit(buffer.position() + bytesRead);
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
