package com.prectice.mfa.Dto;

public class LoginRequest {
    private String username;
    private String password;
    private int totp;

    public LoginRequest() {}

    public LoginRequest(String username, String password, int totp) {
        this.username = username;
        this.password = password;
        this.totp = totp;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public int getTotp() { return totp; }

    public void setTotp(int totp) { this.totp = totp; }
}
