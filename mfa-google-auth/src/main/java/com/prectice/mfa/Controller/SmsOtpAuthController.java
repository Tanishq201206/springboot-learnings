package com.prectice.mfa.Controller;


import com.prectice.mfa.Dto.SmsLoginRequest;
import com.prectice.mfa.Dto.SmsOtpVerifyRequest;
import com.prectice.mfa.Dto.SmsRegisterRequest;
import com.prectice.mfa.Model.SmsOtpUser;
import com.prectice.mfa.Service.JwtService;
import com.prectice.mfa.Service.SmsOtpService;
import com.prectice.mfa.repo.SmsOtpUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sms-auth")
public class SmsOtpAuthController {

    @Autowired
    private SmsOtpUserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SmsOtpService otpService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody SmsRegisterRequest request) {
        SmsOtpUser user = new SmsOtpUser(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhoneNumber()
        );
        userRepo.save(user);
        return ResponseEntity.ok("Registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody SmsLoginRequest request) {
        Optional<SmsOtpUser> userOpt = userRepo.findByUsername(request.getUsername());
        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        SmsOtpUser user = userOpt.get();
        otpService.sendOtp(user.getUsername(), user.getPhoneNumber());

        return ResponseEntity.ok("OTP sent to your phone");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody SmsOtpVerifyRequest request) {
        Optional<SmsOtpUser> userOpt = userRepo.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username");
        }

        if (!otpService.verifyOtp(request.getUsername(), request.getOtp())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
        }

        otpService.clearOtp(request.getUsername());
        String token = jwtService.generateToken(userOpt.get().getUsername());
        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}
