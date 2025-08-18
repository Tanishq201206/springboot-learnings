package com.prectice.mfa.Service;


import org.springframework.stereotype.Service;



import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class SmsOtpService {

    private final Map<String, String> otpStorage = new HashMap<>();



    public void sendOtp(String username, String phone) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        otpStorage.put(username, otp);
        System.out.println("📲 Sending OTP to " + phone + ": " + otp);
    }

    public boolean verifyOtp(String username, String otp) {
        return otp.equals(otpStorage.get(username));
    }

    public void clearOtp(String username) {
        otpStorage.remove(username);
    }
}
