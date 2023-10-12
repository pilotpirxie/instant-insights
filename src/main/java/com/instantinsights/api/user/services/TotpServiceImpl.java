package com.instantinsights.api.user.services;

import com.instantinsights.api.user.exceptions.TotpServiceException;
import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class TotpServiceImpl implements TotpService {
    SecretGenerator secretGenerator = new DefaultSecretGenerator();
    TimeProvider timeProvider = new SystemTimeProvider();
    CodeGenerator codeGenerator = new DefaultCodeGenerator();
    QrGenerator generator = new ZxingPngQrGenerator();

    @Override
    public String generateToken() {
        return secretGenerator.generate();
    }

    @Override
    public String getUriForImage(String token, String email, String issuer) throws TotpServiceException {
        QrData data = new QrData.Builder()
            .label(email)
            .secret(token)
            .issuer(issuer)
            .algorithm(HashingAlgorithm.SHA1)
            .digits(6)
            .period(30)
            .build();

        byte[] imageData;
        try {
            imageData = generator.generate(data);
        } catch (QrGenerationException e) {
            throw new TotpServiceException("Failed to generate QR code", e);
        }

        return getDataUriForImage(imageData);
    }

    @Override
    public boolean verifyCode(String token, String code) {
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        return verifier.isValidCode(token, code);
    }

    static String getDataUriForImage(byte[] bytes) {
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
    }
}
