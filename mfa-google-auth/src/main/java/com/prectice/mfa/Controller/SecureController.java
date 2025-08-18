package com.prectice.mfa.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/secure")
public class SecureController {

    @GetMapping("/hello")
    public String sayHello(Authentication authentication) {
        String username = authentication.getName();
        return "👋 Hello, " + username + "! You have accessed a secured endpoint!";
    }
}
