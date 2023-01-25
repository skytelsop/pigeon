package com.example.pigeon.security;

public interface ISecurityUserService {

    String validatePasswordResetToken(String token);
}
