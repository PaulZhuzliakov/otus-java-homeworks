package hw11;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class MappedByteBufferStorage implements OffHeapStorage {

    private final int capacityBytes;
    private MappedByteBuffer buffer;
    private int dataSize;

    public MappedByteBufferStorage(int capacityBytes) {
        if (capacityBytes <= 0) {
            throw new IllegalArgumentException("размер хранилища должен быть положительным: " + capacityBytes);
        }
        this.capacityBytes = capacityBytes;
    }

    @Override
    public void load(Path file) throws IOException {
        try (FileChannel channel = FileChannel.open(file, StandardOpenOption.READ)) {
            if (channel.size() > capacityBytes) {
                throw new IllegalArgumentException(
                        "файл " + file + " (" + channel.size() + " байт) не влезает в хранилище (" + capacityBytes + " байт)");
            }
            dataSize = (int) channel.size();
            buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
    }

    @Override
    public int size() {
        return dataSize;
    }

    @Override
    public byte readByte(int position) {
        if (buffer == null) {
            throw new IllegalStateException("сначала load");
        }
        if (position < 0 || position >= dataSize) {
            throw new IndexOutOfBoundsException("позиция " + position + " вне данных (0.." + (dataSize - 1) + ")");
        }
        return buffer.get(position);
    }

    @Override
    public byte[] readAll() {
        if (buffer == null) {
            throw new IllegalStateException("сначала load");
        }
        byte[] copy = new byte[dataSize];
        buffer.get(0, copy);
        return copy;
    }
}
