package org.java.expert;

import java.nio.Buffer;
import java.nio.file.NoSuchFileException;

public interface OffHeapStorage {
    Buffer load(String path) throws NoSuchFileException;
}
