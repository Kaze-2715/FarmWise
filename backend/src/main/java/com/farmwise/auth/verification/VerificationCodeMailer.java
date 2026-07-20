package com.farmwise.auth.verification;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VerificationCodeMailer {
    private final JavaMailSender mailSender;

    public void send(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("FarmWise 验证码");
        message.setText("您的验证码是：%s，5 分钟内有效".formatted(code));

        mailSender.send(message);
    }
}
