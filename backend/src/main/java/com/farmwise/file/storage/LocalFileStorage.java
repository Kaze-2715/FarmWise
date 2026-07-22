package com.farmwise.file.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class LocalFileStorage implements FileStorage {
    private static final String PROVIDER = "local";
    private final Path rootDirectory;

    public LocalFileStorage(LocalFileStorageProperties properties) {
        this.rootDirectory = properties.rootDirectory()
                .toAbsolutePath()
                .normalize();
    }

    @PostConstruct
    void initialize() {
        try {
            Files.createDirectories(rootDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException("无法初始化本地文件存储目录", exception);
        }
    }

    @Override
    public String provider() {
        return PROVIDER;
    }

    @Override
    public void store(String storageKey, InputStream inputStream) {
        Path target = resolveTarget(storageKey);

        try {
            Files.createDirectories(target.getParent());
            Files.copy(inputStream, target);
        } catch (IOException exception) {
            throw new UncheckedIOException("文件保存失败", exception);
        }
    }

    @Override
    public Resource load(String storageKey) {
        Path target = resolveTarget(storageKey);

        if (!Files.isRegularFile(target)) {
            throw new IllegalStateException("文件内容不存在");
        }

        return new FileSystemResource(target);
    }

    private Path resolveTarget(String storageKey) {
        Path target = rootDirectory.resolve(storageKey).normalize();

        if (!target.startsWith(rootDirectory)) {
            throw new IllegalArgumentException("文件存储键不合法");
        }

        return target;
    }
}