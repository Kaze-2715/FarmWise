package com.farmwise.file.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.farmwise.file.storage.LocalFileStorageProperties;

@Configuration
@EnableConfigurationProperties(LocalFileStorageProperties.class)
public class FileStorageConfig {
    
}
