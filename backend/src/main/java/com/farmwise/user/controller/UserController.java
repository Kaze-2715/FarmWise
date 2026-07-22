package com.farmwise.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmwise.user.dto.UpdateUserProfileRequest;
import com.farmwise.user.dto.UserProfile;
import com.farmwise.user.service.UserProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserProfileService userProfileService;

    @GetMapping("/me")
    public ResponseEntity<UserProfile> myProfile(Authentication authentication) {
        String userId = authentication.getName();
        UserProfile profile = userProfileService.getUserProfile(userId);

        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfile> updateProfile(
        @Valid @RequestBody UpdateUserProfileRequest request,
        Authentication authentication
    ) {
        String userId = authentication.getName();
        UserProfile profile = userProfileService.updateUserProfile(userId, request);

        return ResponseEntity
                .ok(profile);
    }
}
