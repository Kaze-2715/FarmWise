package com.farmwise.user.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmwise.auth.verification.VerificationCodeStore;
import com.farmwise.common.exception.BizException;
import com.farmwise.file.mapper.FileMapper;
import com.farmwise.user.dto.UpdateUserProfileRequest;
import com.farmwise.user.dto.UserProfile;
import com.farmwise.user.mapper.UserMapper;
import com.farmwise.user.mapper.UserRoleMapper;
import com.farmwise.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final VerificationCodeStore verificationCodeStore;
    private final FileMapper fileMapper;

    private static final String CHANGE_EMAIL_SCENE = "change_email";

    @Transactional(readOnly = true)
    public UserProfile getUserProfile(String userId) {
        User user = findCurrentUser(userId);
        List<String> roles = userRoleMapper.findRoleCodesByUserId(userId);
        List<String> permissions = userRoleMapper.findPermissionCodesByUserId(userId);

        return UserProfile.from(user, roles, permissions);
    }

    @Transactional
    public UserProfile updateUserProfile(String userId,
                UpdateUserProfileRequest request
    ) {
        User currentUser = findCurrentUser(userId);
        User updatedUser = buildUpdatedUser(currentUser, request);

        int updatedRows = userMapper.updateProfile(updatedUser);

        if (updatedRows == 0) {
            throw new BizException(HttpStatus.UNAUTHORIZED, "登录状态已失效");
        }

        if (!currentUser.email().equals(updatedUser.email())) {
            verificationCodeStore.delete(updatedUser.email(), CHANGE_EMAIL_SCENE);
        }

        List<String> roles = userRoleMapper.findRoleCodesByUserId(userId);
        List<String> permissions = userRoleMapper.findPermissionCodesByUserId(userId);

        return UserProfile.from(updatedUser, roles, permissions);
    }

    private User findCurrentUser(String userId) {
        return userMapper.findById(userId).orElseThrow(() -> new BizException(HttpStatus.UNAUTHORIZED, "登录状态已失效"));
    }

    private void validateUniqueFields(String userId, String username, String email) {
        if (userMapper.existsByUsernameAndIdNot(username, userId)) {
            throw new BizException(HttpStatus.CONFLICT, "用户名已被使用");
        }

        if (userMapper.existsByEmailAndIdNot(email, userId)) {
            throw new BizException(HttpStatus.CONFLICT, "邮箱已被使用");
        }
    }

    private boolean resolveEmailVerified(
            User currentUser,
            String newEmail,
            String verificationCode) {
        if (currentUser.email().equals(newEmail)) {
            return currentUser.emailVerified();
        }
        if (verificationCode == null || verificationCode.isBlank()) {
            throw new BizException(HttpStatus.BAD_REQUEST, "修改邮箱时必须提交验证码");
        }

        if (!verificationCodeStore.matches(newEmail, CHANGE_EMAIL_SCENE, verificationCode)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "验证码错误或已过期");
        }

        return true;
    }

    private void validateAvatarFile(
            String userId,
            String avatarFileId) {
        if (avatarFileId == null) {
            return;
        }

        if (!fileMapper.existsAvatarByIdAndOwnerId(avatarFileId, userId)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "头像文件无效");
        }
    }

    private User buildUpdatedUser(
            User currentUser,
            UpdateUserProfileRequest request) {
        String username = request.username().trim();
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String realName = normalizeOptional(request.realName());
        String phone = normalizeOptional(request.phone());
        String organization = normalizeOptional(request.organization());
        String province = normalizeOptional(request.province());
        String city = normalizeOptional(request.city());
        String position = normalizeOptional(request.position());
        String avatarFileId = normalizeOptional(request.avatarFileId());

        validateUniqueFields(currentUser.id(), username, email);

        boolean emailVerified = resolveEmailVerified(currentUser, email, request.verificationCode());

        validateAvatarFile(currentUser.id(), avatarFileId);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        return currentUser.toBuilder()
                .username(username)
                .email(email)
                .emailVerified(emailVerified)
                .realName(realName)
                .phone(phone)
                .organization(organization)
                .province(province)
                .city(city)
                .position(position)
                .avatarFileId(avatarFileId)
                .updatedAt(now)
                .build();
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        if (value.isBlank()) {
            return null;
        }
        return value;
    }
}
