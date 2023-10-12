package com.instantinsights.api.event.controllers;

import com.instantinsights.api.email.services.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController()
@RequestMapping("/events")
class EventController {

    private final EmailService emailService;

    public EventController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/test")
    public Map<String, String> test() {
        Map<String, String> env = new HashMap<>();
        env.put("test", "test");

        emailService.sendEmail(null, null, null);

        return env;
    }
}
