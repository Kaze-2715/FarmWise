package com.farmwise.file.model;

import org.springframework.core.io.Resource;

public record FileContent(
    Resource resource,
    String contentType,
    long size
) {
    
}
