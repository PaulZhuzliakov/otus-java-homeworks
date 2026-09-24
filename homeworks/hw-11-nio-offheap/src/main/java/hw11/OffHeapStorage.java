package hw11;

import java.io.IOException;
import java.nio.file.Path;

public interface OffHeapStorage {

    void load(Path file) throws IOException;

    int size();

    byte readByte(int position);

    byte[] readAll();
}
