package com.farmwise.auth.service;

import java.security.SecureRandom;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.farmwise.auth.dto.VerificationCodeRequest;
import com.farmwise.auth.verification.VerificationCodeMailer;
import com.farmwise.auth.verification.VerificationCodeStore;
import com.farmwise.common.exception.BizException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationCodeService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final VerificationCodeStore verificationCodeStore;
    private final VerificationCodeMailer verificationCodeMailer;

    public void create(VerificationCodeRequest request, Authentication authentication) {
        if ("change_email".equals(request.scene())
                && (authentication == null
                || authentication instanceof AnonymousAuthenticationToken)) {
            throw new BizException(HttpStatus.UNAUTHORIZED, "修改邮箱前必须登录");
        }

        String code = "%06d".formatted(SECURE_RANDOM.nextInt(1_000_000));

        verificationCodeStore.save(request.email(), request.scene(), code);

        verificationCodeMailer.send(request.email(), code);
    }
}
