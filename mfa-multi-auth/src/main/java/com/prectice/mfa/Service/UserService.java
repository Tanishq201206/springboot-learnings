package com.prectice.mfa.Service;


import com.prectice.mfa.Dto.RegisterRequest;
import com.prectice.mfa.Model.User;
import com.prectice.mfa.repo.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String register(RegisterRequest request) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        GoogleAuthenticatorKey key = gAuth.createCredentials();

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                key.getKey(),
                true
        );

        userRepository.save(user);

        String qrUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL("SpringBootMFA", request.getUsername(), key);
        return "✅ Registration successful!\n\n" +
                "📎 Secret Key: " + key.getKey() + "\n" +
                "📷 QR Code URL (scan in Google Authenticator):\n" + qrUrl + "\n\n" +
                "📌 If QR doesn't scan, manually enter the above key.";
    }



    public String loginAndReturnToken(String username, String password, int totp) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) throw new RuntimeException("Invalid credentials");

        User user = userOpt.get();

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new RuntimeException("Invalid credentials");

        if (user.isMfaEnabled()) {
            GoogleAuthenticator gAuth = new GoogleAuthenticator();
            boolean isCodeValid = gAuth.authorize(user.getSecretKey(), totp);
            if (!isCodeValid) throw new RuntimeException("Invalid OTP");
        }

        return jwtService.generateToken(user.getUsername());
    }


}
