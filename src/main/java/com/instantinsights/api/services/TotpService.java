package com.instantinsights.api.services;

public interface TotpService {
    String generateToken();

    String getUriForImage(String token, String email);

    boolean verifyCode(String token, int code);
}
