package com.farmwise.file.storage;

import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "farmwise.file.storage.local")
public record LocalFileStorageProperties(
    Path rootDirectory
) {
    
}
