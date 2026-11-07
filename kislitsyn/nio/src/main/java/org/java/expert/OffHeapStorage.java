package org.java.expert;

import java.nio.ByteBuffer;
import java.nio.file.NoSuchFileException;

public interface OffHeapStorage {
    ByteBuffer load(String path) throws NoSuchFileException;
}
