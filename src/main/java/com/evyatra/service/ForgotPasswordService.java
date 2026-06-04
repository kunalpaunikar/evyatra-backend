package com.evyatra.service;

import com.evyatra.exception.BadRequestException;
import com.evyatra.exception.ResourceNotFoundException;
import com.evyatra.model.PasswordResetOtp;
import com.evyatra.model.User;
import com.evyatra.repository.OtpRepository;
import com.evyatra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // Step 1 — generate OTP and Send Email
    public String sendOtp(String email) {

        // Is User exists ?
        userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ye email registered nahi hai!"
                ));

        // Generate 6 digit OTP
        String otp = String.format("%06d",
                new Random().nextInt(999999)
        );

        // Save OTP
        PasswordResetOtp otpEntity = new PasswordResetOtp();
        otpEntity.setEmail(email);
        otpEntity.setOtp(otp);
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(10));
        otpEntity.setUsed(false);
        otpRepository.save(otpEntity);

        // Send Email
        emailService.sendOtpEmail(email, otp);

        return "OTP SENT - CHECK EMAIL!";
    }

    // Step 2 — Verify OTP
    public String verifyOtp(String email, String otp) {

        PasswordResetOtp otpEntity = otpRepository
                .findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new BadRequestException(
                        "OTP not found - send first!"
                ));

        // OTP is used already ?
        if (otpEntity.isUsed()) {
            throw new BadRequestException("OTP is used already!");
        }

        // Expire OTP ?
        if (LocalDateTime.now().isAfter(otpEntity.getExpiryTime())) {
            throw new BadRequestException("OTP expired - please send again!");
        }

        // Match OTP
        if (!otpEntity.getOtp().equals(otp)) {
            throw new BadRequestException("Incorrect OTP!");
        }

        return "OTP verified!";
    }

    // Step 3 — Reset Password
    public String resetPassword(String email, String otp, String newPassword) {

        // Verify OTP
        verifyOtp(email, otp);

        // Password validation
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,12}$";

        if (!newPassword.matches(regex)) {
            throw new BadRequestException(
                    "Password should be 8-12 chars, contain 1 digit and 1 special character!"
            );
        }

        // Search User and Update Password
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Not Found User !"
                ));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // OTP mark as used
        PasswordResetOtp otpEntity = otpRepository
                .findTopByEmailOrderByCreatedAtDesc(email)
                .get();
        otpEntity.setUsed(true);
        otpRepository.save(otpEntity);

        return "Password is reset successfully!";
    }
}