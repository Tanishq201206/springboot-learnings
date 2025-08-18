package com.prectice.mfa.Controller;


import com.prectice.mfa.Dto.LoginRequest;
import com.prectice.mfa.Dto.RegisterRequest;
import com.prectice.mfa.Model.User;
import com.prectice.mfa.Service.UserService;
import com.prectice.mfa.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        String qrUrl = userService.register(request);
        return ResponseEntity.ok("Scan this QR with Google Authenticator:\n" + qrUrl);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String jwt = userService.loginAndReturnToken(
                    request.getUsername(),
                    request.getPassword(),
                    request.getTotp()
            );

            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("❌ " + e.getMessage());
        }


    }

}
