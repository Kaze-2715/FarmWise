package com.farmwise.file.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.farmwise.file.dto.FileResource;
import com.farmwise.file.model.FileContent;
import com.farmwise.file.service.FileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResource> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("purpose") String purpose,
            Authentication authentication) {
        String userId = authentication.getName();

        FileResource resource = fileService.upload(userId, file, purpose);

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @GetMapping("/{fileId}/content")
    public ResponseEntity<Resource> content(
            @PathVariable("fileId") String fileId) {
        FileContent content = fileService.loadContent(fileId);

        MediaType mediaType = MediaType.parseMediaType(content.contentType());

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .contentLength(content.size())
                .body(content.resource());
    }
}