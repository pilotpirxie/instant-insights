package com.instantinsights.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController()
@RequestMapping("/events")
class EventController {

    @GetMapping("/test")
    public Map<String, String> test() {
        Map<String, String> env = new HashMap<>();
        env.put("test", "test");
        return env;
    }
}
