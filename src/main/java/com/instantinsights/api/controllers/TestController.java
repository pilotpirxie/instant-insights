package com.instantinsights.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
class TestController {

    @GetMapping("/test")
    public Map<String, String> test() {
        Map<String, String> env = new HashMap<>();
        env.put("test", "test");
        return env;
    }
}
