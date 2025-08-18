package com.prectice.mfa.Controller;


import com.prectice.mfa.Dto.EmailLoginRequest;
import com.prectice.mfa.Dto.EmailOtpVerificationRequest;
import com.prectice.mfa.Dto.EmailRegisterRequest;
import com.prectice.mfa.Model.EmailOtpUser;
import com.prectice.mfa.Service.JwtService;
import com.prectice.mfa.Service.OtpService;
import com.prectice.mfa.repo.EmailOtpUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/email-auth")
public class EmailOtpAuthController {

    @Autowired
    private EmailOtpUserRepository emailUserRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody EmailRegisterRequest request) {
        if (emailUserRepo.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists");
        }

        EmailOtpUser user = new EmailOtpUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());

        emailUserRepo.save(user);

        return ResponseEntity.ok("Registration successful. You can now log in.");
    }


    @PostMapping("/login/init")
    public ResponseEntity<String> initiateLogin(@RequestBody EmailLoginRequest request) {
        Optional<EmailOtpUser> userOpt = emailUserRepo.findByUsername(request.getUsername());
        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        EmailOtpUser user = userOpt.get();
        otpService.sendOtpToEmail(user.getUsername(), user.getEmail());
        return ResponseEntity.ok("OTP sent to your email.");
    }

    @PostMapping("/login/verify")
    public ResponseEntity<?> verifyOtpAndLogin(@RequestBody EmailOtpVerificationRequest request) {
        Optional<EmailOtpUser> userOpt = emailUserRepo.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user");
        }

        if (!otpService.verifyOtp(request.getUsername(), request.getOtp())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired OTP");
        }

        otpService.clearOtp(request.getUsername());
        String token = jwtService.generateToken(request.getUsername());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}
