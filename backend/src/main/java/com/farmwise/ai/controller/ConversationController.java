package com.farmwise.ai.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.farmwise.ai.dto.ConversationResponse;
import com.farmwise.ai.dto.ConversationSummaryResponse;
import com.farmwise.ai.dto.CreateConversationRequest;
import com.farmwise.ai.dto.CreateTaskFromMessageRequest;
import com.farmwise.ai.dto.SendMessageRequest;
import com.farmwise.ai.dto.SendMessageResponse;
import com.farmwise.ai.service.ConversationService;
import com.farmwise.security.permission.RequiredPermission;
import com.farmwise.task.dto.FarmTaskResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ai/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    @GetMapping
    @RequiredPermission("ai_advisor:use")
    public ResponseEntity<List<ConversationSummaryResponse>> listConversations(
            @RequestParam String landId,
            @RequestParam(required = false) String status,
            Authentication authentication) {
        String userId = authentication.getName();
        List<ConversationSummaryResponse> response = conversationService.listConversations(
                userId,
                landId,
                status);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{conversationId}")
    @RequiredPermission("ai_advisor:use")
    public ResponseEntity<ConversationResponse> getConversation(
            @PathVariable String conversationId,
            Authentication authentication) {
        ConversationResponse response = conversationService.getConversation(
                authentication.getName(),
                conversationId);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @RequiredPermission("ai_advisor:use")
    public ResponseEntity<ConversationResponse> createConversation(
            @Valid @RequestBody CreateConversationRequest request,
            Authentication authentication) {
        String userId = authentication.getName();

        ConversationResponse response = conversationService.createConversation(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/{conversationId}/messages")
    @RequiredPermission("ai_advisor:use")
    public ResponseEntity<SendMessageResponse> sendMessage(
            @PathVariable String conversationId,
            @Valid @RequestBody SendMessageRequest request,
            Authentication authentication) {
        String userId = authentication.getName();

        SendMessageResponse response = conversationService.sendMessage(
                userId,
                conversationId,
                request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{conversationId}/messages/{messageId}/task")
    @RequiredPermission("farm_task:manage")
    public ResponseEntity<FarmTaskResponse> createTaskFromMessage(
            @PathVariable String conversationId,
            @PathVariable String messageId,
            @Valid @RequestBody CreateTaskFromMessageRequest request,
            Authentication authentication) {

        String userId = authentication.getName();

        FarmTaskResponse response = conversationService.createTaskFromMessage(
                userId,
                conversationId,
                messageId,
                request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/{conversationId}/close")
    @RequiredPermission("ai_advisor:use")
    public ResponseEntity<Void> closeConversation(
            @PathVariable String conversationId,
            Authentication authentication) {

        String userId = authentication.getName();

        conversationService.closeConversation(
                userId,
                conversationId);

        return ResponseEntity.noContent().build();
    }
}
