package com.evyatra.controller;

import com.evyatra.service.ForgotPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    // Step 1 — Send OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestBody Map<String, String> body) {
        String result = forgotPasswordService.sendOtp(body.get("email"));
        return ResponseEntity.ok(result);
    }

    // Step 2 — verify the OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @RequestBody Map<String, String> body) {
        String result = forgotPasswordService.verifyOtp(
                body.get("email"),
                body.get("otp")
        );
        return ResponseEntity.ok(result);
    }

    // Step 3 —  reset the Password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody Map<String, String> body) {
        String result = forgotPasswordService.resetPassword(
                body.get("email"),
                body.get("otp"),
                body.get("newPassword")
        );
        return ResponseEntity.ok(result);
    }
}