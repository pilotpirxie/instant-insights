package com.instantinsights.api.email.services;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
}
