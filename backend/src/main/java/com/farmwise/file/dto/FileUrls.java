package com.farmwise.file.dto;

public final class FileUrls {
    private FileUrls() {
    }
    
    public static String content(String fileId) {
        return "/api/files/%s/content".formatted(fileId);
    }
}
