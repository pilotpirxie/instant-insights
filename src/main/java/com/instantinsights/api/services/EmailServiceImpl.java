package com.instantinsights.api.services;

import com.instantinsights.api.config.EmailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    JavaMailSender mailer;
    EmailProperties emailProperties;

    public EmailServiceImpl(JavaMailSender mailer, EmailProperties emailProperties) {
        this.mailer = mailer;
        this.emailProperties = emailProperties;
    }

    @Override
    public void sendEmail(String to, String subject, String text) {
        if (to == null || to.isEmpty() || subject == null || subject.isEmpty() || text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Email address cannot be null or empty");
        }

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(text);
        email.setFrom(emailProperties.getUsername());

        mailer.send(email);
    }
}
