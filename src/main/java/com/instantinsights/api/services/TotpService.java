package com.instantinsights.api.services;

import com.instantinsights.api.exceptions.TotpServiceException;

public interface TotpService {
    String generateToken();

    String getUriForImage(String token, String email, String issuer) throws TotpServiceException;

    boolean verifyCode(String token, String code);
}
