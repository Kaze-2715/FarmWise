package com.farmwise.file.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.farmwise.common.exception.BizException;
import com.farmwise.file.dto.FileResource;
import com.farmwise.file.dto.FileUrls;
import com.farmwise.file.mapper.FileMapper;
import com.farmwise.file.model.FileContent;
import com.farmwise.file.model.FileMetadata;
import com.farmwise.file.storage.FileStorage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {
    private static final String AVATAR_PURPOSE = "avatar";
    private static final long MAX_AVATAR_SIZE = 5 * 1024 * 1024;
    private static final Set<String> AVATAR_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final FileMapper fileMapper;
    private final FileStorage fileStorage;

    @Transactional
    public FileResource upload(String userId, MultipartFile file, String purpose) {
        validateUpload(file, purpose);

        String id = UUID.randomUUID().toString();
        String storageKey = "%s/%s".formatted(purpose, id);
        String originalName = resolveOriginalName(file);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        FileMetadata metadata = new FileMetadata(id, userId, originalName, file.getContentType(),
                file.getSize(), purpose, fileStorage.provider(), storageKey, now);

        int insertedRows = fileMapper.insert(metadata);

        if (insertedRows != 1) {
            throw new IllegalStateException("文件元数据保存失败");
        }

        try (InputStream inputStream = file.getInputStream()) {
            fileStorage.store(storageKey, inputStream);
        } catch (IOException exception) {
            throw new UncheckedIOException("读取文件上传失败", exception);
        }

        String url = FileUrls.content(id);

        return FileResource.from(metadata, url);
    }

    @Transactional(readOnly = true)
    public FileContent loadContent(String fileId) {
        FileMetadata metadata = fileMapper
        .findById(fileId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "文件不存在"));
        
        if (!fileStorage.provider().equals(metadata.storageProvider())) {
            throw new IllegalStateException("文件存储实现不可用");
        }

        Resource resource = fileStorage.load(metadata.storageKey());

        return new FileContent(resource, metadata.contentType(), metadata.size());
    }

    private String resolveOriginalName(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            return "avatar";
        }
        originalName = StringUtils.cleanPath(originalName.trim());
        originalName = StringUtils.getFilename(originalName);

        if (originalName.isBlank()) {
            return "avatar";
        }

        if (originalName.length() > 255) {
            throw new BizException(HttpStatus.BAD_REQUEST, "文件名不能超过 255 个字符");
        }

        return originalName;
    }

    private void validateUpload(MultipartFile file, String purpose) {
        if (!AVATAR_PURPOSE.equals(purpose)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "文件用途不支持");
        }

        if (file == null || file.isEmpty()) {
            throw new BizException(HttpStatus.BAD_REQUEST, "文件不能为空");
        }

        if (file.getSize() > MAX_AVATAR_SIZE) {
            throw new BizException(HttpStatus.BAD_REQUEST, "头像文件大小不能超过 5 MB");
        }

        if (!AVATAR_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "头像只支持 JPEG、PNG 和 WebP 格式");
        }
    }
}
