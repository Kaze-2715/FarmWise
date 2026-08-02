package com.farmwise.auth.verification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class VerificationCodeMailer {
    private final JavaMailSender mailSender;
    private final String fromAddress;

    public VerificationCodeMailer(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void send(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromAddress);
        message.setTo(email);
        message.setSubject("FarmWise 验证码");
        message.setText("您的验证码是：%s，5 分钟内有效".formatted(code));

        mailSender.send(message);
    }
}
