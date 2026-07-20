package com.farmwise.auth.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmwise.auth.dto.LoginRequest;
import com.farmwise.auth.dto.LoginResponse;
import com.farmwise.common.exception.BizException;
import com.farmwise.security.jwt.JwtTokenService;
import com.farmwise.user.dto.UserProfile;
import com.farmwise.user.mapper.UserMapper;
import com.farmwise.user.mapper.UserRoleMapper;
import com.farmwise.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {
    private static final String TOKEN_TYPE = "Bearer";

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = findUser(request);

        validateCredentials(user, request.password());

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        userMapper.updateLastLoginAt(user.id(), now);

        User loggedInUser = user.withLastLoginAt(now);

        return createResponse(loggedInUser);
    }

    @Transactional(readOnly = true)
    public LoginResponse refresh(String userId) {
        User user = userMapper
                .findById(userId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.UNAUTHORIZED,
                        "刷新令牌无效或已过期"));

        if (!"active".equals(user.status())) {
            throw new BizException(HttpStatus.FORBIDDEN, "账号已被禁用");
        }

        return createResponse(user);
    }

    private User findUser(LoginRequest request) {
        String account = request.account().trim();

        Optional<User> user;

        if ("email".equals(request.loginType())) {
            String email = account.toLowerCase(Locale.ROOT);
            user = userMapper.findByEmail(email);
        } else {
            user = userMapper.findByUsername(account);
        }

        return user.orElseThrow(() -> new BizException(HttpStatus.UNAUTHORIZED, "账号或密码错误"));
    }

    private void validateCredentials(
            User user,
            String rawPassword
    ) {
        if (!passwordEncoder.matches(rawPassword, user.passwordHash())) {
            throw new BizException(HttpStatus.UNAUTHORIZED, "账号或密码错误");
        }

        if (!"active".equals(user.status())) {
            throw new BizException(HttpStatus.FORBIDDEN, "账号已被禁用");
        }
    }

    private LoginResponse createResponse(User user) {
        List<String> roles = userRoleMapper.findRoleCodesByUserId(user.id());

        List<String> permissions =
                userRoleMapper.findPermissionCodesByUserId(user.id());

        String accessToken = jwtTokenService.createAccessToken(user.id(), permissions);

        UserProfile userProfile = UserProfile.from(user, roles, permissions);

        return new LoginResponse(
                accessToken,
                TOKEN_TYPE,
                jwtTokenService.accessTokenTtlSeconds(),
                userProfile);
    }
}
