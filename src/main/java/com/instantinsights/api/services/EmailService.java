package com.instantinsights.api.services;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
}
