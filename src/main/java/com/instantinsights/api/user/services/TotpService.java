package com.instantinsights.api.user.services;

import com.instantinsights.api.user.exceptions.TotpServiceException;

public interface TotpService {
    String generateToken();

    String getUriForImage(String token, String email, String issuer) throws TotpServiceException;

    boolean verifyCode(String token, String code);
}
