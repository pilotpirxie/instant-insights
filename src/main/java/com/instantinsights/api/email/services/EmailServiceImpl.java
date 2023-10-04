package com.instantinsights.api.email.services;

import com.instantinsights.api.common.config.EmailConfig;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    JavaMailSender mailer;
    EmailConfig emailConfig;

    public EmailServiceImpl(JavaMailSender mailer, EmailConfig emailConfig) {
        this.mailer = mailer;
        this.emailConfig = emailConfig;
    }

    @Override
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(text);
        email.setFrom(emailConfig.getUsername());

        mailer.send(email);
    }
}
