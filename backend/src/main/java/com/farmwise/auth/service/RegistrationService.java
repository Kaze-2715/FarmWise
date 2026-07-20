package com.farmwise.auth.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmwise.auth.dto.RegisterRequest;
import com.farmwise.auth.verification.VerificationCodeStore;
import com.farmwise.common.exception.BizException;
import com.farmwise.user.dto.UserProfile;
import com.farmwise.user.mapper.UserMapper;
import com.farmwise.user.mapper.UserRoleMapper;
import com.farmwise.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private static final String REGISTER_SCENE = "register";
    private static final String DEFAULT_ROLE_CODE = "farm_owner";

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final VerificationCodeStore verificationCodeStore;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserProfile register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (userMapper.existsByEmail(email)) {
            throw new BizException(HttpStatus.CONFLICT, "邮箱已被注册");
        }

        if (!verificationCodeStore.matches(
                email,
                REGISTER_SCENE,
                request.verificationCode())) {
            throw new BizException(HttpStatus.BAD_REQUEST, "验证码错误或已过期");
        }

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        User user = User.registered(
                UUID.randomUUID().toString(),
                generateUsername(),
                passwordEncoder.encode(request.password()),
                email,
                now);

        userMapper.insert(user);
        userRoleMapper.insert(user.id(), DEFAULT_ROLE_CODE, now);

        List<String> roles = userRoleMapper.findRoleCodesByUserId(user.id());

        List<String> permissions =
                userRoleMapper.findPermissionCodesByUserId(user.id());

        verificationCodeStore.delete(email, REGISTER_SCENE);

        return UserProfile.from(user, roles, permissions);
    }

    private String generateUsername() {
        String username;

        do {
            String randomPart = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 12);

            username = "user_" + randomPart;
        } while (userMapper.existsByUsername(username));

        return username;
    }
}
