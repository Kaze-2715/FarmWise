package com.farmwise.file.storage;

import java.io.InputStream;

import org.springframework.core.io.Resource;

public interface FileStorage {
    String provider();

    void store(String storageKey, InputStream inputStream);

    Resource load(String storageKey);
}
