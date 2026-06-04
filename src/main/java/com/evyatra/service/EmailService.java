package com.evyatra.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("⚡ EVyatra — Password Reset OTP");
        message.setText(
                "Hello! \n\n" +
                        "Your EVYatra Password Reset OTP is:\n\n" +
                        "🔐 OTP: " + otp + "\n\n" +
                        "This OTP is valid for 10 minutes.\n" +
                        "Do not share with anyone !\n\n" +
                        "— EVyatra Team ⚡"
        );
        mailSender.send(message);
    }
}