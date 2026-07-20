package com.farmwise.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmwise.auth.dto.LoginRequest;
import com.farmwise.auth.dto.LoginResponse;
import com.farmwise.auth.dto.RegisterRequest;
import com.farmwise.auth.dto.VerificationCodeRequest;
import com.farmwise.auth.service.LoginService;
import com.farmwise.auth.service.RegistrationService;
import com.farmwise.auth.service.VerificationCodeService;
import com.farmwise.common.exception.BizException;
import com.farmwise.security.token.RefreshTokenCookieService;
import com.farmwise.security.token.RefreshTokenService;
import com.farmwise.user.dto.UserProfile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final VerificationCodeService verificationCodeService;

    private final RegistrationService registrationService;

    private final LoginService loginService;

    private final RefreshTokenService refreshTokenService;

    private final RefreshTokenCookieService refreshTokenCookieService;

    @PostMapping("/verification-codes")
    public ResponseEntity<Void> sendVerificationCode(
            @Valid @RequestBody VerificationCodeRequest request,
            Authentication authentication
    ) {
        verificationCodeService.create(request, authentication);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/register")
    public ResponseEntity<UserProfile> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        UserProfile userProfile = registrationService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userProfile);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = loginService.login(request);

        String refreshToken = refreshTokenService.issue(response.user().id());

        ResponseCookie cookie = refreshTokenCookieService.create(refreshToken);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @CookieValue(
                    name = "${farmwise.security.refresh-token.cookie-name}",
                    required = false
            ) String oldRefreshToken
    ) {
        if (oldRefreshToken == null || oldRefreshToken.isBlank()) {
            throw new BizException(HttpStatus.UNAUTHORIZED, "刷新令牌无效或已过期");
        }

        String userId = refreshTokenService.consume(oldRefreshToken);

        LoginResponse response = loginService.refresh(userId);

        String newRefreshToken = refreshTokenService.issue(userId);

        ResponseCookie cookie = refreshTokenCookieService.create(newRefreshToken);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(
                    name = "${farmwise.security.refresh-token.cookie-name}",
                    required = false
            ) String refreshToken
    ) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.revoke(refreshToken);
        }

        ResponseCookie cookie = refreshTokenCookieService.delete();

        return ResponseEntity
                .noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
