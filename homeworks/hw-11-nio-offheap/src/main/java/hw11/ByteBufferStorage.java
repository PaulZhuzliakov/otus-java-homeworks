package hw11;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ByteBufferStorage implements OffHeapStorage {

    private final ByteBuffer buffer;
    private int dataSize;

    public ByteBufferStorage(int capacityBytes) {
        if (capacityBytes <= 0) {
            throw new IllegalArgumentException("размер хранилища должен быть положительным: " + capacityBytes);
        }
        this.buffer = ByteBuffer.allocateDirect(capacityBytes);
    }

    @Override
    public void load(Path file) throws IOException {
        buffer.clear();
        try (FileChannel channel = FileChannel.open(file, StandardOpenOption.READ)) {
            if (channel.size() > buffer.capacity()) {
                throw new IllegalArgumentException(
                        "файл " + file + " (" + channel.size() + " байт) не влезает в хранилище (" + buffer.capacity() + " байт)");
            }
            while (buffer.hasRemaining()) {
                if (channel.read(buffer) == -1) {
                    break;
                }
            }
            dataSize = buffer.position();
            buffer.flip();
        }
    }

    @Override
    public int size() {
        return dataSize;
    }

    @Override
    public byte readByte(int position) {
        if (position < 0 || position >= dataSize) {
            throw new IndexOutOfBoundsException("позиция " + position + " вне данных (0.." + (dataSize - 1) + ")");
        }
        return buffer.get(position);
    }

    @Override
    public byte[] readAll() {
        byte[] copy = new byte[dataSize];
        buffer.get(0, copy);
        return copy;
    }
}
