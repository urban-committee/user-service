package com.blogs.userservice.entity;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
    private int verificationCode; // 2FA code
}

